package de.skiptag.whenjs.js;

import java.net.URI;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

class ModuleScriptProvider extends SoftCachingModuleScriptProvider {

	private final ResourceLoader resourceLoader;
	private static final long serialVersionUID = 1L;

	public ModuleScriptProvider(ResourceLoader resourceLoader) {
		super(new UrlModuleSourceProvider(null, null));
		this.resourceLoader = resourceLoader;
	}

	@Override
	public ModuleScript getModuleScript(Context cx, String moduleId, URI uri, URI base, Scriptable paths)
			throws Exception {

		CachedModuleScript cachedModule = getLoadedModule(moduleId);
		if (cachedModule != null) {
			return super.getModuleScript(cx, moduleId, uri, uri, paths);
		}

		if (uri == null) {

			URL url = resourceLoader.getURL(moduleId);

			if (url != null) {
				uri = url.toURI();
			}
		}
		return super.getModuleScript(cx, moduleId, uri, uri, paths);
	}
}