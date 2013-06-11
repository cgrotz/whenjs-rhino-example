package de.skiptag.whenjs.js;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConstantMethods {
	// Parallel running Threads(Executor) on System
	public static int corePoolSize = 2;

	// Maximum Threads allowed in Pool
	public static int maxPoolSize = 4;

	// Keep alive time for waiting threads for jobs(Runnable)
	public static long keepAliveTime = 10;

	public static ThreadPoolExecutor threadPool;
	static {
		// Working queue for jobs (Runnable). We add them finally here
		ArrayBlockingQueue workQueue = new ArrayBlockingQueue(5);

		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	}

	public static void setTimeout(final Function func, final int timeout) {
		final ScriptableObject scope = RhinoRunner.getScope();
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				RhinoRunner.scope.set(scope);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
				}
				func.call(Context.enter(), scope, scope, null);
			}
		});
	}
}
