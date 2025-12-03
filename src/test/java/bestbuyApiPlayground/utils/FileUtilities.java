package bestbuyApiPlayground.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(FileUtilities.class);
	
	public String readFile(String path) throws IOException {
		logger.debug("FileUtilities: readFile" + path);
		 return new String(Files.readAllBytes(Paths.get(path)));
	}

}
