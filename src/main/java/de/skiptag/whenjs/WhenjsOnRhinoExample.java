package de.skiptag.whenjs;

import de.skiptag.whenjs.js.RhinoRunner;

public class WhenjsOnRhinoExample {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		RhinoRunner rhinoRunner = new RhinoRunner("main.js");
		rhinoRunner.start();
		while (true) {

		}
	}
}
