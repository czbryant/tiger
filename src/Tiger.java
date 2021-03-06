import java.io.*;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import control.CommandLine;
import control.Control;
import parser.Parser;

public class Tiger 
{
	static Tiger tiger;
	static CommandLine cmd;
	static InputStream fstream;
	public ast.program.T theAst;
	
	// lex and parse
	public void lexAndParse(String fname)
	{
		Parser parser;
	    try 
	    {
	    	fstream = new BufferedInputStream(new FileInputStream(fname));
	    	parser = new Parser(fname, fstream);

	    	System.out.println("Testing the parser:");
	    	theAst = parser.parse();

	    	fstream.close();
	    	System.out.println("Testing the parser is finished.\n");
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	    return;
	}
	
	public void compile(String fname)
	{
		// /////////////////////////////////////////////////////
	    // to test the pretty printer on the "test/Fac.java" program
	    if(control.Control.testFac) 
	    {
	    	ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
	    	control.CompilerPass ppPass = new control.CompilerPass("Pretty printing AST", ast.Fac.prog, pp);
	    	// ppPass.doit();

	    	// elaborate the given program, this step is necessary
	    	// for that it will annotate the AST with some
	    	// informations used by later phase.
	    	elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
	    	control.CompilerPass elabPass = new control.CompilerPass("Elaborating the AST", ast.Fac.prog, elab);
	    	elabPass.doit();

	    	// optimize the AST
	    	ast.optimizations.Main optAstPasses = new ast.optimizations.Main();
	    	control.CompilerPass optAstPass = new control.CompilerPass("Optimizing AST", optAstPasses, ast.Fac.prog);
	    	optAstPass.doit();
	    	ast.Fac.prog = (ast.program.Program)optAstPasses.program;

	    	// Compile this program to C.
	    	codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
	    	control.CompilerPass genCCodePass = new control.CompilerPass("Translation to C code", ast.Fac.prog, transC);
	    	genCCodePass.doit();
	    	codegen.C.program.T cAst = transC.program;

	    	if(control.Control.dumpC) 
	    	{
	    		codegen.C.PrettyPrintVisitor ppC = new codegen.C.PrettyPrintVisitor();
	    		control.CompilerPass ppCCodePass = new control.CompilerPass("C code printing", cAst, ppC);
	    		ppCCodePass.doit();
	    	}

	    	// translation to control-flow graph
	    	cfg.TranslateVisitor transCfg = new cfg.TranslateVisitor();
	    	control.CompilerPass genCfgCodePass = new control.CompilerPass("Control-flow graph generation", cAst, transCfg);
	    	genCfgCodePass.doit();
	    	cfg.program.T cfgAst = transCfg.program;

	    	// visualize the control-flow graph, if necessary
	    	if(control.Control.visualize != Control.Visualize_Kind_t.None) 
	    	{
	    		cfg.VisualVisitor toDot = new cfg.VisualVisitor();
	    		control.CompilerPass genDotPass = new control.CompilerPass("Draw control-flow graph", cfgAst, toDot);
	    		genDotPass.doit();
	    	}

	    	// optimizations on the control-flow graph
	    	cfg.optimizations.Main cfgOpts = new cfg.optimizations.Main();
	    	control.CompilerPass cfgOptPass = new control.CompilerPass("Control-flow graph optimizations", cfgOpts, cfgAst);
	    	cfgOptPass.doit();

	    	// code generation
	    	switch(control.Control.codegen) 
	    	{
	    	case Bytecode:
	    		codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
	    		control.CompilerPass genBytecodePass = new control.CompilerPass("Bytecode generation", ast.Fac.prog, trans);
	    		genBytecodePass.doit();
	    		codegen.bytecode.program.T bytecodeAst = trans.program;

	    		codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
	    		control.CompilerPass ppBytecodePass = new control.CompilerPass("Bytecode printing", bytecodeAst, ppbc);
	    		ppBytecodePass.doit();
	    		break;
	    	case C:
	    		cfg.PrettyPrintVisitor ppCfg = new cfg.PrettyPrintVisitor();
	    		control.CompilerPass ppCfgCodePass = new control.CompilerPass("C code printing", cfgAst, ppCfg);
	    		ppCfgCodePass.doit();
	    		break;
	    	case Dalvik:
	    		// similar
	    		break;
	    	case X86:
	    		// similar
	    		break;
	    	default:
	    		break;
	    	}
	    	return;
	    }

	    // /////////////////////////////////////////////////////
	    // normal test
	    if(fname == null) 
	    {
	    	cmd.usage();
	    	return;
	    }
	    Control.fileName = fname;

	    // /////////////////////////////////////////////////////
	    // it would be helpful to be able to test the lexer independently.
	    if(control.Control.testlexer) 
	    {
	    	System.out.println("Testing the lexer. All tokens:");
	    	try 
	    	{
	    		fstream = new BufferedInputStream(new FileInputStream(fname));
	    		Lexer lexer = new Lexer(fname, fstream);
	    		Token token = lexer.nextToken();
	    		
	    		while(token.kind != Kind.TOKEN_EOF) 
	    		{
	    			System.out.println(token.toString());
	    			token = lexer.nextToken();
	    		}
	    		System.out.println(token.toString());
	    		fstream.close();
	    		System.out.println("Testing the lexer is finished.\n");
	    	} 
	    	catch(Exception e) 
	    	{
	    		e.printStackTrace();
	    	}
	    	System.exit(1);
	    }
	    
	    // /////////////////////////////////////////////////////////
	    // normal compilation phases.
	    theAst = null;

	    control.CompilerPass lexAndParsePass = new control.CompilerPass("Lex and parse", tiger, fname);
	    lexAndParsePass.doitName("lexAndParse");

	    // pretty printing the AST, if necessary
	    if(control.Control.dumpAst) 
	    {
	    	ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
	    	control.CompilerPass ppAstPass = new control.CompilerPass("Pretty printing the AST", theAst, pp);
	    	ppAstPass.doit();
	    }

	    // elaborate the AST, report all possible errors.
	    elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
	    control.CompilerPass elabAstPass = new control.CompilerPass("Elaborating the AST", theAst, elab);
	    elabAstPass.doit();

	    // optimize the AST
	    ast.optimizations.Main optAstPasses = new ast.optimizations.Main();
	    control.CompilerPass optAstPass = new control.CompilerPass("Optimizing the AST", optAstPasses, theAst);
	    optAstPass.doit();
	    theAst = optAstPasses.program;

	    // Compile this program to C.
    	codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
    	control.CompilerPass genCCodePass = 
    			new control.CompilerPass("Translation to C code",  theAst, transC);
    	genCCodePass.doit();
    	codegen.C.program.T cAst = transC.program;

    	if(control.Control.dumpC) 
    	{
    		codegen.C.PrettyPrintVisitor ppC = new codegen.C.PrettyPrintVisitor();
    		control.CompilerPass ppCCodePass = new control.CompilerPass("C code printing", cAst, ppC);
    		ppCCodePass.doit();
    	}

    	// translation to control-flow graph
    	cfg.TranslateVisitor transCfg = new cfg.TranslateVisitor();
    	control.CompilerPass genCfgCodePass = new control.CompilerPass("Control-flow graph generation", cAst, transCfg);
    	genCfgCodePass.doit();
    	cfg.program.T cfgAst = transCfg.program;

    	// visualize the control-flow graph, if necessary
    	if(control.Control.visualize != Control.Visualize_Kind_t.None) 
    	{
    		cfg.VisualVisitor toDot = new cfg.VisualVisitor();
    		control.CompilerPass genDotPass = new control.CompilerPass("Draw control-flow graph", cfgAst, toDot);
    		genDotPass.doit();
    	}

    	// optimizations on the control-flow graph
    	cfg.optimizations.Main cfgOpts = new cfg.optimizations.Main();
    	control.CompilerPass cfgOptPass = new control.CompilerPass("Control-flow graph optimizations", cfgOpts, cfgAst);
    	cfgOptPass.doit();
    	
    	cfgAst=cfgOpts.program;
    	
	    // code generation
	    switch(control.Control.codegen)
	    {
	    case Bytecode:
	    	codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
	    	control.CompilerPass genBytecodePass = new control.CompilerPass("Bytecode generation", theAst, trans);
	    	genBytecodePass.doit();
	    	codegen.bytecode.program.T bytecodeAst = trans.program;

	    	codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
	    	control.CompilerPass ppBytecodePass = new control.CompilerPass("Bytecode printing", bytecodeAst, ppbc);
	    	ppBytecodePass.doit();
	    	break;
	    case C:
	    	cfg.PrettyPrintVisitor ppCfg = new cfg.PrettyPrintVisitor();
    		control.CompilerPass ppCfgCodePass = new control.CompilerPass("C code printing", cfgAst, ppCfg);
    		ppCfgCodePass.doit();
    		break;
	    case Dalvik:
	    	codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
	        control.CompilerPass genDalvikCodePass = new control.CompilerPass(
	            "Dalvik code generation", theAst, transDalvik);
	        genDalvikCodePass.doit();
	        codegen.dalvik.program.T dalvikAst = transDalvik.program;

	        codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
	        control.CompilerPass ppDalvikCodePass = new control.CompilerPass(
	            "Dalvik code printing", dalvikAst, ppDalvik);
	        ppDalvikCodePass.doit();
	        break;
	    case X86:
	    	// similar
	    	break;
	    default:
	    	break;
	    }

	    
	    Runtime run = Runtime.getRuntime();
		Process p = null;
		try {
			String param[] = fname.split("/");
			String filename = param[param.length - 1];
			BufferedReader br = null;
			switch (control.Control.codegen) {
			case C:
				String str = "gcc " + fname + ".c" + " runtime/runtime.c";
				// String params[] = {"/bin/sh","-c",str};
				p = run.exec(str);
				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				if (br != null) {
					System.out.println(br.readLine());
					for (String buf = br.readLine(); buf != null; buf = br
							.readLine()) {
						System.out.println(buf);
					}
				}
				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				
				p = run.exec("a.exe @tiger -gcLog @20");
				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				break;
			case Bytecode:
				filename = filename.substring(0, filename.length() - 5);
				// System.out.println(filename);
				// String parms[] = {"java","-jar","../jasmin.jar","../bin*.j"};
				String parms[] = { "/bin/sh", "-c",
						"java -jar ../jasmin.jar ../bin/*.j" };
				p = run.exec(parms);

				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}

				p = run.exec("java " + filename);
				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				for (String buf = br.readLine(); buf != null; buf = br
						.readLine()) {
					System.out.println(buf);
				}
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ;
	}
	
	public void assemble(String str)
	{
		// Your code here:
	}
	
	public void link(String str)
	{
	    // Your code here:
	}
	
	public void compileAndLink(String fname)
	{
		// compile
	    control.CompilerPass compilePass = new control.CompilerPass("Compile", tiger, fname);
	    compilePass.doitName("compile");

	    // assembling
	    control.CompilerPass assemblePass = new control.CompilerPass("Assembling", tiger, fname);
	    assemblePass.doitName("assemble");

	    // linking
	    control.CompilerPass linkPass = new control.CompilerPass("Linking", tiger, fname);
	    linkPass.doitName("link");

	    return;
	}
	
	public static void main(String[] args) 
	{
		// ///////////////////////////////////////////////////////
	    // handle command line arguments
	    tiger = new Tiger();
	    cmd = new CommandLine();
	    String fname = "";
	    fname = cmd.scan(args);

	    control.CompilerPass tigerAll = new control.CompilerPass("Tiger", tiger, fname);
	    tigerAll.doitName("compileAndLink");
	    return;
	}
}