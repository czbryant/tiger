#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#ifdef WIN32
#include <Windows.h>
LONGLONG g_Time_Start;
LONGLONG g_Time_End;
LONGLONG g_Time_Freq;
#endif // WIN32
#ifdef linux
#include <unistd.h>
#include <sys/time.h>
struct timeval g_start;
struct timeval g_end;
#endif // LINUX
#define START_COUNT 1
#define END_COUNT 2
double g_Time_Dur;
//#define PRINT_NEWINFO

// The Gimple Garbage Collector.
static void Tiger_gc();
static int g_iRound = 0;
//===============================================================//
// The Java Heap data structure.

/*
 ----------------------------------------------------
 |                        |                         |
 ----------------------------------------------------
 ^\                      /^
 | \<~~~~~~~ size ~~~~~>/ |
 from                       to
 */
struct JavaHeap {
	int size; // in bytes, note that this if for semi-heap size
	char *from; // the "from" space pointer
	char *fromFree; // the next "free" space in the from space
	char *to; // the "to" space pointer
	char *toStart; // "start" address in the "to" space
	char *toNext; // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init(int heapSize) {

	int *object = (int *) malloc(heapSize);
	// #2: initialize the "size" field, note that "size" field
	// is for semi-heap, but "heapSize" is for the whole heap.
	//printf("%d\n",object);

	heap.size = heapSize / 2;
	// #3: initialize the "from" field (with what value?)
	heap.from = (int *) heap.from;
	heap.from = (int) object;
	// #4: initialize the "fromFree" field (with what value?)
	heap.fromFree = (int *) heap.fromFree;
	heap.fromFree = (int) object;
	// #5: initialize the "to" field (with what value?)
	heap.to = heap.from + heap.size;
	// #6: initizlize the "toStart" field with NULL;
	heap.toStart = NULL;
	// #7: initialize the "toNext" field with NULL;
	heap.toNext = NULL;
	return;
}

// The "prev" pointer, pointing to the top frame on the GC stack.
// (see part A of Lab 4)
void *prev = 0;

//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
 | vptr      ---|----> (points to the virtual method table)
 |--------------|
 | isObjOrArray | (0: for normal objects)
 |--------------|
 | length       | (this field should be empty for normal objects)
 |--------------|
 | forwarding   |
 |--------------|\
p---->| v_0          | \
      |--------------|  s
 | ...          |  i
 |--------------|  z
 | v_{size-1}   | /e
 ----------------/
 */
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the appropiate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1;
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new(void *vtable, int size) {

	int *p;
	int free_space = heap.from + heap.size - heap.fromFree;
	if (Control_printNewInfo) {
		printf("-------new object------\n");
		printf("the object size=%d\n", size);
		printf("the heap size left=%d\n", free_space);
	}
	if (heap.to - heap.fromFree < size) {
		Tiger_gc();
		free_space = heap.from + heap.size - heap.fromFree;
		if (size > free_space) {
			printf("Out of memory..\n");
			exit(-1);
		}
	}
	memset(heap.fromFree, 0, size);
	p = (int *) heap.fromFree;
	*(int*) (heap.fromFree + 1) = 0;
	*(int*) (heap.fromFree + 2) = NULL;
	*(int*) (heap.fromFree + 3) = NULL;
	*(int *) heap.fromFree = (int) vtable;
	heap.fromFree = heap.fromFree + size;

	return (void *) p;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information.
/*    ----------------
 | vptr         | (this field should be empty for an array)
 |--------------|
 | isObjOrArray | (1: for array)
 |--------------|
 | length       |
 |--------------|
 | forwarding   |
 |--------------|\
p---->| e_0          | \
      |--------------|  s
 | ...          |  i
 |--------------|  z
 | e_{length-1} | /e
 ----------------/
 */
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the appropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1;
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
static int count = 0;
void *Tiger_new_array(int length) {
	// Your code here:


	int *p;
	int free_space = heap.from + heap.size - heap.fromFree;
	int array_size = sizeof(int) * (4 + length);

	if (Control_printNewInfo) {
			printf("-------new array No.%d,length=%d------\n", count++, length);
			printf("the size of array =%d\n", array_size);
			printf("heap size left=%d\n", free_space);
		}

	if (heap.to - heap.fromFree < array_size) {
		Tiger_gc();
		free_space = heap.from + heap.size - heap.fromFree;
		if (free_space < array_size) {
			printf("Sorry,out of memory\n");
			exit(-1);
		}

	}
	memset(heap.fromFree, 0, array_size);
	p = (int *) heap.fromFree;
	*(int *) heap.fromFree = NULL;
	*(p + 1) = 1;
	*(p + 2) = length;
	*(p + 3) = NULL;

	heap.fromFree = heap.fromFree + array_size;

	return (void *) p;
}

void gcTimeCount(int type) {


#ifdef WIN32
	LARGE_INTEGER li;
	if(type==START_COUNT)
	{
		QueryPerformanceCounter(&li);
		g_Time_Start = li.QuadPart;
	}
	else
	{
		QueryPerformanceFrequency(&li);
		g_Time_Freq=li.QuadPart;
		QueryPerformanceCounter(&li);
		g_Time_End=li.QuadPart;
		g_Time_Dur=(double)(g_Time_End-g_Time_Start)/g_Time_Freq;
	}
#endif
#ifdef linux
	if(type==START_COUNT)
	gettimeofday(&g_start,NULL);
	else
	{
		gettimeofday(&g_end,NULL);
		g_Time_Dur=(g_end.tv_sec-g_start.tv_sec)*1000000
		+(g_end.tv_usec-g_start.tv_usec);
		g_Time_Dur/=1000000;
	}
#endif
}
void Format_OutBuf(char *buf_out) {
	//malloc the Control_logInfo
	if (Control_logInfo == NULL) {
		Control_logInfo = (char*) malloc(Control_bufSize);
	}
	int buf_size = strlen(buf_out) + strlen(Control_logInfo);
	//realloc the memory for Control_logInfo
	if (buf_size >= Control_bufSize) {
		Control_bufSize = buf_size + 256;
		Control_logInfo = (char*) realloc(Control_logInfo, Control_bufSize);
		strcat(Control_logInfo, buf_out);
		return;
	} else {
		strcat(Control_logInfo, buf_out);
		//printf("The collect log :%s\n", Control_logInfo);
	}
}
void Tiger_gcLog() {
	FILE *pFileOut = fopen("gcLog.txt", "wb+");
	if (!pFileOut) {
		printf("Open or create log file failed..\n");
		exit(-1);
	}

	if (Control_logInfo == NULL) {
		printf("Please to use a.exe @tiger -gcLog @ to get the log info! \n");
	} else {
		if (strlen(Control_logInfo) == 0) {

			strcpy(Control_logInfo,
					"gc not called,please resize the 'Control_heapSize' to a smaller one..\n'");
		}
		fputs(Control_logInfo, pFileOut);
		printf("please check gcLog.txt for the log..\n");
		free(Control_logInfo);
		fclose(pFileOut);
	}

}
//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.

int Forward(void *p) {
	int *pForward;
	char *pClass_gc_map;
	int obj_size;
	//check if p points to from-heap
	if (p >= (void*) heap.from && p < (void*) heap.from + heap.size)
	{

		pForward = (int*) p + 3;
		if (*pForward >= (int) heap.to && *pForward < (int) heap.to + heap.size)
		{
			//check if p.forward points to to-heap then return
			return *pForward;
		} else {
			obj_size = 4;//set size equals to object's header value;
			*pForward = (int) heap.toNext;//points to it's own start position in the to-heap
			if (0 == *((int*) p + 1))
			{
				//this is a normal object
				pClass_gc_map = (char*) (*(int*) (*(int*) p));
				obj_size += strlen(pClass_gc_map);
				memcpy(heap.toNext, p, obj_size * sizeof(int));
			} else /*if(1==*((int*)p+1))//this is an array*/
			{
				obj_size += *(pForward - 1);//plus the value of "length"
				memcpy(heap.toNext, p, obj_size * sizeof(int));
			}

			heap.toNext = heap.toNext + obj_size * sizeof(int);
		}

		return *pForward;

	}
	return (int) p;
}
static void Tiger_gc() {
	// Your code here:
	char *pScan, *pArgs_gc_map, *pLocals_gc_map, *pClass_gc_map;
	int tmp;
	char *pTmp;
	int *pArgs, *pLocals, *pScan_V;
	void *pPrev_Old;
	char buf_out[200];
	int scan_obj_size, scan_size_Total;
	if (Control_isOutLog) {
		gcTimeCount(START_COUNT);
	}
	heap.toNext = heap.to;
	pScan = heap.toNext;
	scan_size_Total = 0;
	printf("gc is called\n");
	pPrev_Old = prev;
	while (prev) {
		pArgs_gc_map = (char*) (*((int*) prev + 1));
		pArgs = (int*) (*((int*) prev + 2));
		pLocals_gc_map = (char*) (*((int*) prev + 3));
		pLocals = (int*) prev + 4;
		while (*pArgs_gc_map != '\0') {
			if ('1' == *pArgs_gc_map) {
				*pArgs = Forward((int*) (*pArgs));
			}
			pArgs++;
			pArgs_gc_map++;
		}
		//locals forward
		while (*pLocals_gc_map != '\0') {
			if ('1' == *pLocals_gc_map) {
				*pLocals = Forward((int*) (*pLocals));
				pLocals++;
			}
			pLocals_gc_map++;
		}
		prev = (void*) (*(int*) prev);
	}
	prev = pPrev_Old;//restore prev
	while (pScan < heap.toNext) {
		scan_obj_size = 4;
		if (0 == *((int*) pScan + 1))//for normal object type
		{
			pScan_V = (int*) pScan + 4;
			pClass_gc_map = (char*) (*(int*) (*(int*) pScan));
			while (*pClass_gc_map != '\0') {
				if ('1' == *pClass_gc_map) {
					*pScan_V = Forward((int*) (*pScan_V));
				}

				scan_obj_size++;
				pScan_V++;
				pClass_gc_map++;
			}
		} else /*if(*((int*)pScan+1)==1)*/
		{
			scan_obj_size += *((int*) pScan + 2);

		}
		pScan = pScan + scan_obj_size * sizeof(int);
		scan_size_Total += scan_obj_size;

	}
	/*swap heap pointer*/
	tmp = heap.fromFree - heap.from;
	pTmp = heap.from;
	heap.from = heap.to;
	heap.to = pTmp;
	heap.fromFree = heap.toNext;
	if (Control_isOutLog) {
		gcTimeCount(END_COUNT);
		//the collect bytes is the heap.fromFree - scan_size_total
		sprintf(buf_out, "%02d round of GC: %.8fs, collected %d bytes\n",
				++g_iRound, g_Time_Dur, tmp -(scan_size_Total )* sizeof(int));
		printf("%s\n", buf_out);
		Format_OutBuf(buf_out);
	}

}

