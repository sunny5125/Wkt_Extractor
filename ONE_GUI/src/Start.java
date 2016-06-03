import main.DTNSimGUI;
import core.Settings;
import core.SettingsError;


public class Start {
	
	public static void main(String[] args) {
		boolean batchMode = false;
		int nrofRuns[] = {0,1};
		String confFiles[];
		int firstConfIndex = 0;
		int guiIndex = 0;

		/* set US locale to parse decimals in consistent way */
		java.util.Locale.setDefault(java.util.Locale.US);
		
		if (args.length > 0) {
			confFiles = args;
		}
		else {
			confFiles = new String[] {null};
		}
		
		initSettings(confFiles, firstConfIndex);
		
		new DTNSimGUI().start();
	}

	/**
	 * Initializes Settings
	 * @param confFiles File name paths where to read additional settings 
	 * @param firstIndex Index of the first config file name
	 */
	private static void initSettings(String[] confFiles, int firstIndex) {
		int i = firstIndex;
		try {
			Settings.init(confFiles[i]);
			for (i=firstIndex+1; i<confFiles.length; i++) {
				Settings.addSettings(confFiles[i]);
			}
		}
		catch (SettingsError er) {
			try {
				Integer.parseInt(confFiles[i]);
			}
			catch (NumberFormatException nfe) {
				/* was not a numeric value */
				System.err.println("Failed to load settings: " + er);
				System.err.println("Caught at " + er.getStackTrace()[0]);			
				System.exit(-1);
			}
			System.err.println("Warning: using deprecated way of " + 
					"expressing run indexes. Run index should be the " + 
					"first option, or right after -b option (optionally " +
					"as a range of start and end values).");
			System.exit(-1);
		}
	}
	
}
