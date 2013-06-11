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

	private Map<String, Object> attributes = Maps.newHashMap();

	private String scriptName;

	public RhinoRunner(String scriptName) {
		this.resourceLoader = new ResourceLoader();
		this.scriptName = scriptName;
	}

	public RhinoRunner withAttribute(String key, Object value) {
		attributes.put(key, value);
		return this;
	}

	private void addStandardObjectsToScope(ScriptableObject scope) {
		Object console = Context.javaToJS(new Console(), scope);
		ScriptableObject.putProperty(scope, "console", console);

		for (String key : attributes.keySet()) {
			Object value = Context.javaToJS(attributes.get(key), scope);
			ScriptableObject.putProperty(scope, key, value);
		}
	}

	// Support for loading from CommonJS modules
	private Require installRequire(Context cx, ScriptableObject scope) {
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
			addStandardObjectsToScope(scope.get());

			scope.get().defineFunctionProperties(new String[] { "setTimeout" }, ConstantMethods.class,
					ScriptableObject.DONTENUM);

			Require require = installRequire(cx, scope.get());

			Scriptable script = require.requireMain(cx, scriptName);
		} finally {
			Context.exit();
		}
	}

	public static ScriptableObject getScope() {
		return scope.get();
	}
}