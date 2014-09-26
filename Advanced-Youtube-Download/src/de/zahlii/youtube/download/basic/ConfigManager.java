package de.zahlii.youtube.download.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used for storing and retrieving the Config entries. Every entry
 * is associated with exactly one ConfigKey.
 * 
 * @author Zahlii
 * 
 */
public class ConfigManager {
	public static final String DS = File.separator;

	public static final File YOUTUBE_DL = new File("youtube-dl.exe");
	public static final File MP3GAIN = new File("mp3gain.exe");
	public static final File FFMPEG = new File("ffmpeg.exe");
	public static final File TEMP_DIR = new File("temp");
	public static final File METAFLAC = new File("metaflac.exe");

	private static ConfigManager instance;

	private static File configFile = new File("ytload.config");
	private static String encoding = "UTF-8";

	private final Map<ConfigKey, String> config;

	private ConfigManager() {
		this.config = new HashMap<ConfigKey, String>();
		this.loadFile();
	}

	private void loadFile() {

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}

			final byte[] encoded = Files.readAllBytes(Paths.get(configFile
					.toURI()));
			final String[] lines = new String(encoded, encoding).split("\n");
			for (final String line : lines) {
				final String[] parts = line.split("=");
				if (parts.length < 2) {
					continue;
				}
				this.config.put(ConfigKey.valueOf(parts[0]),
						parts[1].replace("\r", "").replace("\n", ""));
			}
		} catch (final IOException e) {
			Logging.log("failed loading config file", e);
		}
	}

	private void writeFile() {

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}

			final StringBuilder sb = new StringBuilder();
			for (final Entry<ConfigKey, String> e : this.config.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("\n");
			}

			final FileWriter f2 = new FileWriter(configFile, false);
			f2.write(sb.toString());
			f2.close();

		} catch (final IOException e) {
			Logging.log("failed writing config file", e);
		}
	}

	public void setConfig(final ConfigKey key, final String value) {
		this.config.put(key, value);
		this.writeFile();
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}

		return instance;
	}

	public String getConfig(final ConfigKey key, final String def) {
		if (!this.config.containsKey(key)) {
			this.setConfig(key, def);
			return def;
		} else
			return this.config.get(key);
	}

	public enum ConfigKey {
		AUDIO_BITRATE, DIR_TARGET, FILENAME_CONVENTION, DIR_IMAGES, IS_DEFAULT, KEEP_VIDEO, IMPROVE_CONVERT, VOLUME_METHOD
	}

}
