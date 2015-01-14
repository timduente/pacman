package game.player.ghost.group4;

import game.player.ghost.group4.system.ZCSEntry;
import game.player.ghost.group4.system.ZCSObservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ExternalClassifierParser implements IClassifierDataSource {

	static final int PARAMETER_COUNT = 4;
	static final int OBSERVATION_INDEX = 0;
	static final int WILDCARD_INDEX = 1;
	static final int ACTION_INDEX = 2;
	static final int FITNESS_INDEX = 3;

	String filePath = null;
	boolean IS_BINARY_STRING_MODE = true;

	public ExternalClassifierParser(String pfilePath) {
		filePath = pfilePath;
	}

	@Override
	public List<ZCSEntry> getSomeData() {

		List<ZCSEntry> result = new ArrayList<ZCSEntry>();

		// try load file

		File f = new File(filePath);

		if (!f.exists()) {
			return result; // no data available
		}

		BufferedReader br = null;

		try {
			// open
			br = new BufferedReader(new FileReader(f));

			// read content an parse to classifier
			int ctr = 0;

			String line = null;
			while ((line = br.readLine()) != null) {

				// get classifier parameters
				String[] params = line.split(";");

				if (params.length != PARAMETER_COUNT) {
					continue; // errornous line
				}

				// parse data and convert to classifier
				int[] iParams = new int[params.length];
				for (int i = 0; i < params.length; ++i) {
					
					int parsed = 0;

					if (IS_BINARY_STRING_MODE && i == OBSERVATION_INDEX)
						parsed = BinaryStr2Int(params[i]);
					else if (IS_BINARY_STRING_MODE && i == WILDCARD_INDEX)
						parsed = BinaryWildcardStr2Int(params[i]);
					else
						parsed = Integer.parseInt(params[i]);

					iParams[i] = parsed;
				}

				try {
					ZCSEntry entry = makeClassifierFromInputData(iParams);
					result.add(entry);
					ctr++;
				} catch (Exception ex) {
					// skip errornous entry
					System.out.println(">> an error occured wile importing classifier: " + line);
				}
			}

			System.out.println(">> imported " + ctr + " classifier from: " + filePath);

			// close
			if (br != null) {
				br.close();
			}
		} catch (Exception e) {
			System.out.println(">> error while importing classifierdata: " + filePath);
			e.printStackTrace();
		}

		return result;
	}

	private ZCSEntry makeClassifierFromInputData(int[] params) {

		ZCSObservation obsRightEscape = new ZCSObservation(params[OBSERVATION_INDEX], params[WILDCARD_INDEX]);
		return new ZCSEntry(obsRightEscape, new GhostAction(params[ACTION_INDEX]), params[FITNESS_INDEX]);
	}

	private String classifierToStringLine(ZCSEntry entry, String[] params, boolean stringMode) {

		if (stringMode) {
			params[OBSERVATION_INDEX] = INT2BinaryStr(entry.getObservation().getBits());
			params[WILDCARD_INDEX] = INT2BinaryWildcardStr(entry.getObservation().getWildcards());
		} else {
			params[OBSERVATION_INDEX] = "" + entry.getObservation().getBits();
			params[WILDCARD_INDEX] = "" + entry.getObservation().getWildcards();
		}

		params[ACTION_INDEX] = "" + entry.getAction().getActionBits();
		params[FITNESS_INDEX] = "" + entry.getFitness();

		String result = "";
		for (int i = 0; i < params.length; ++i) {
			result += params[i] + ';';
		}

		return result;

	}

	public void export(ICSDatabaseSource database, String pFilePath) {

		if (pFilePath == null)
			pFilePath = filePath;

		List<ZCSEntry> source = database.getClassifierDatabase();
		if (source == null)
			return;

		String[] paramsBuffer = new String[PARAMETER_COUNT]; // speicher wiederverwenden
		StringBuilder result = new StringBuilder(source.size());
		final String newLineSymbol = System.lineSeparator(); // java 1.7 (http://stackoverflow.com/questions/19084352/how-to-write-new-line-character-to-a-file-in-java)

		try {
			for (ZCSEntry entry : source) {
				result.append(classifierToStringLine(entry, paramsBuffer, IS_BINARY_STRING_MODE) + newLineSymbol);
			}
		} catch (Exception ex) {
			System.out.println(">> error during export --> loop interrupted, maybe another thread still manipulated it ...");
		}

		File file = new File(pFilePath);
		if (!file.exists()) {
			System.out.println(">> new export file: " + pFilePath + " ...");

			// create file (http://stackoverflow.com/questions/6142901/how-to-create-a-file-in-a-directory-in-java)
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println(">> creating new file failed");
				e.printStackTrace();
				return; // abort
			}
		} else {
			// simply make a backup
			//String dstName = file.toPath() + file.getName() + "-BACKUP";
			String dstName = file.getName() + "-BACKUP";
			try {
				Files.copy(file.toPath(), (new File(dstName)).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println(">> creating backupfile failed");
				e.printStackTrace();
			}
		}

		// export-vorgang
		try {

			// write single string
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(result.toString());

			if (bw != null) {
				bw.flush();
				bw.close();
			}

			System.out.println(">> export to" + pFilePath + " done");
		} catch (Exception e) {
			System.out.println(">> exception during export data may be invalid!");
			e.printStackTrace();
		}
	}

	public static String INT2BinaryStr(int value) {

		StringBuilder sb = new StringBuilder(32);
		final int mask = 1;

		for (int i = 31; i >= 0; i--) {
			final int shiftErg = value >> i;
			if ((shiftErg & mask) == mask)
				sb.append('1');
			else
				sb.append('0');
		}

		return sb.toString();
	}

	public static String INT2BinaryWildcardStr(int value) {

		StringBuilder sb = new StringBuilder(32);
		final int mask = 1;

		for (int i = 31; i >= 0; i--) {
			final int shiftErg = value >> i;
			if ((shiftErg & mask) == mask)
				sb.append('#'); // 1 = #
			else
				sb.append('0');
		}

		return sb.toString();
	}

	public static int BinaryStr2Int(String bitStr) {

		int result = 0;

		for (int i = 0; i < bitStr.length(); ++i) {

			final int mask = bitStr.charAt(i) != 0 ? 1 : 0;
			result |= mask << i;
		}

		return result;
	}

	public static int BinaryWildcardStr2Int(String wildcardStr) {

		int result = 0;

		for (int i = 0; i < wildcardStr.length(); ++i) {

			final int mask = wildcardStr.charAt(i) == '#' ? 1 : 0;
			result |= mask << i;
		}

		return result;
	}

}
