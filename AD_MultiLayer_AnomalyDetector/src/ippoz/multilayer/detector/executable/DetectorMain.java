/**
 * 
 */
package ippoz.multilayer.detector.executable;

import ippoz.multilayer.detector.manager.DetectionManager;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.PreferencesManager;

/**
 * @author Tommy
 *
 */
public class DetectorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreferencesManager prefManager;
		DetectionManager dManager;
		try {
			prefManager = new PreferencesManager("detector.preferences");
			AppLogger.logInfo(DetectorMain.class, "Preferences Loaded");
			dManager = new DetectionManager(prefManager);
			if(dManager.needTest()) {
				AppLogger.logInfo(DetectorMain.class, "Starting Train Process");
				dManager.train();
			} 
			AppLogger.logInfo(DetectorMain.class, "Starting Evaluation Process");
			dManager.evaluate();
			AppLogger.logInfo(DetectorMain.class, "Done.");
			dManager.printDetails();
		} catch(Exception ex) {
			AppLogger.logException(DetectorMain.class, ex, "");
		}
	}
	
}
