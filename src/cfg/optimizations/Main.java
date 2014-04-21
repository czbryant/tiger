package cfg.optimizations;

public class Main {
	public cfg.program.T program;

	public void accept(cfg.program.T cfg) {
		// liveness analysis
		LivenessVisitor liveness = new LivenessVisitor();
		control.CompilerPass livenessPass = new control.CompilerPass(
				"Liveness analysis", cfg, liveness);
		if (control.Control.skipPass("cfg.Linvess")) {
		} else {
			livenessPass.doit();
		}

		// dead-code elimination
		DeadCode deadCode = new DeadCode();
		control.CompilerPass deadCodePass = new control.CompilerPass(
				"Dead-code elimination", cfg, deadCode);
		if (control.Control.skipPass("cfg.deadCode")) {
		} else {
			deadCode.stmLiveOut = liveness.stmLiveOut;
			deadCodePass.doit();
			cfg = deadCode.program;
		}

		// reaching definition
		ReachingDefinition reachingDef = new ReachingDefinition();
		control.CompilerPass reachingDefPass = new control.CompilerPass(
				"Reaching definition", cfg, reachingDef);
		if (control.Control.skipPass("cfg.reaching")) {
		} else {
			reachingDefPass.doit();
		}

		// constant propagation
		ConstProp constProp = new ConstProp();
		control.CompilerPass constPropPass = new control.CompilerPass(
				"Constant propagation", cfg, constProp);
		if (control.Control.skipPass("cfg.constProp")) {
		} else {
			constProp.stmIn = reachingDef.stmIn;
			constPropPass.doit();
			cfg = constProp.program;
		}

		// liveness analysis
		LivenessVisitor liveness_1 = new LivenessVisitor();
		control.CompilerPass livenessPass_1 = new control.CompilerPass(
				"Liveness analysis", cfg, liveness_1);
		if (control.Control.skipPass("cfg.Linvess")) {
		} else {
			livenessPass_1.doit();
		}

		// dead-code elimination
		DeadCode deadCode_1 = new DeadCode();
		control.CompilerPass deadCodePass_1 = new control.CompilerPass(
				"Dead-code elimination", cfg, deadCode_1);
		if (control.Control.skipPass("cfg.deadCode")) {
		} else {
			deadCode_1.stmLiveOut = liveness_1.stmLiveOut;
			deadCodePass_1.doit();
			cfg = deadCode_1.program;
		}

		ReachingDefinition reachingDef_1 = new ReachingDefinition();
		control.CompilerPass reachingDefPass_1 = new control.CompilerPass(
				"Reaching definition", cfg, reachingDef_1);
		if (control.Control.skipPass("cfg.reaching")) {
		} else {
			reachingDefPass_1.doit();
		}

		// copy propagation
		CopyProp copyProp = new CopyProp();
		control.CompilerPass copyPropPass = new control.CompilerPass(
				"Copy propagation", cfg, copyProp);
		if (control.Control.skipPass("cfg.copyProp")) {
		} else {
			copyProp.stmIn = reachingDef_1.stmIn;
			copyPropPass.doit();
			cfg = copyProp.program;
		}

		// liveness analysis
		LivenessVisitor liveness_2 = new LivenessVisitor();
		control.CompilerPass livenessPass_2 = new control.CompilerPass(
				"Liveness analysis", cfg, liveness_2);
		if (control.Control.skipPass("cfg.Linvess")) {
		} else {
			livenessPass_2.doit();
		}

		// dead-code elimination
		DeadCode deadCode_2 = new DeadCode();
		control.CompilerPass deadCodePass_2 = new control.CompilerPass(
				"Dead-code elimination", cfg, deadCode_2);
		if (control.Control.skipPass("cfg.deadCode")) {
		} else {
			deadCode_2.stmLiveOut = liveness_2.stmLiveOut;
			deadCodePass_2.doit();
			cfg = deadCode_2.program;
		}

		ReachingDefinition reachingDef_2 = new ReachingDefinition();
		control.CompilerPass reachingDefPass_2 = new control.CompilerPass(
				"Reaching definition", cfg, reachingDef_2);
		if (control.Control.skipPass("cfg.reaching")) {
		} else {
			reachingDefPass_2.doit();
		}

		// available expression
		AvailExp availExp = new AvailExp();
		control.CompilerPass availExpPass = new control.CompilerPass(
				"Available expression", cfg, availExp);
		if (control.Control.skipPass("cfg.availExp")) {
		} else {
			availExpPass.doit();
		}

		// CSE
		Cse cse = new Cse();
		control.CompilerPass csePass = new control.CompilerPass(
				"Common subexpression elimination", cfg, cse);
		if (control.Control.skipPass("cfg.cse")) {
		} else {
			cse.stmIn = availExp.stmIn;
			csePass.doit();
			cfg = cse.program;
		}

		program = cfg;

		return;
	}
}
