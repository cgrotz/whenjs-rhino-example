package de.skiptag.whenjs.js;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

import com.google.common.collect.Maps;

public class RhinoRunner {
	private ResourceLoader resourceLoader;

	public static ThreadLocal<ScriptableObject> scope = new ThreadLocal<>();

	private String scriptName;

	public RhinoRunner(String scriptName) {
		this.resourceLoader = new ResourceLoader();
		this.scriptName = scriptName;
	}
    
	private void addConsoleToScope(ScriptableObject scope) {
		Object console = Context.javaToJS(new Console(), scope);
		ScriptableObject.putProperty(scope, "console", console);
	}

	private Require createAndInstallRequire(Context cx, ScriptableObject scope) {
		RequireBuilder rb = new RequireBuilder();
		rb.setSandboxed(false);
		rb.setModuleScriptProvider(new ModuleScriptProvider(resourceLoader));
        
		Require require = rb.createRequire(cx, scope);
		return require;
	}

	@SuppressWarnings("unused")
	public void start() throws Exception {
		Context cx = Context.enter();
		cx.setOptimizationLevel(2);
		try {
			scope.set(cx.initStandardObjects());
			addConsoleToScope(scope.get());

			scope.get().defineFunctionProperties(new String[] { "setTimeout" }, ConstantMethods.class,
					ScriptableObject.DONTENUM);

			Require require = createAndInstallRequire(cx, scope.get());

			Scriptable script = require.requireMain(cx, scriptName);
		} finally {
			Context.exit();
		}
	}

	public static ScriptableObject getScope() {
		return scope.get();
	}
}