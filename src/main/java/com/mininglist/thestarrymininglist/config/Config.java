package com.mininglist.thestarrymininglist.config;

import com.mininglist.thestarrymininglist.TheStarryMiningList;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.io.*;

//配置类
public class Config {
    public static final String CONFIG_FILE_NAME = "miningList.properties";
    public Properties mProp;
    public static String key_display_name = "ScoreboardDisplayName";
    File file;

    private void CreateDefaultConfigFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            String DEFAULT_CONFIG_DATA =
                    "ScoreboardDisplayName = MiningList\n" +
                            "ScoreboardName = MiningList";
            writer.write(DEFAULT_CONFIG_DATA);
        } catch (Exception e) {
            TheStarryMiningList.LOGGER.warn("Config file write error.");
        }
    }//创建默认的配置文件

    public Config(final String filePath) {
        file = new File(filePath + "\\" + CONFIG_FILE_NAME);
        if (!file.exists())
        {
            try {
                CreateDefaultConfigFile(file);
            } catch (Exception e) {
                TheStarryMiningList.LOGGER.warn("warn");
            }
        }

        this.mProp = new Properties();

        try (Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()),
                StandardCharsets.UTF_8)) {
            this.mProp.load(reader);
        } catch (Exception e) {
            TheStarryMiningList.LOGGER.warn("warn");
        }
    }

    public String GetValue(final String key) {
        return this.mProp.getProperty(key);
    }

    public void UpdateValue(final String key, final String val) throws IOException {
        this.mProp.setProperty(key, val);
        this.mProp.store(Files.newOutputStream(this.file.toPath()), null);
    }
}
