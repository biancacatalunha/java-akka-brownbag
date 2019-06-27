package bankaccount.domain.siterep;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Objects;

public class BuoyConfig {

    public BuoyConfigObject getConfig() {
        ObjectMapper mapper = new ObjectMapper();
        BuoyConfigObject configObject = new BuoyConfigObject();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        try {
            configObject = mapper.readValue(new File(Objects.requireNonNull(classLoader.getResource("buoyConfig.json")).getFile()), BuoyConfigObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configObject;
    }
}