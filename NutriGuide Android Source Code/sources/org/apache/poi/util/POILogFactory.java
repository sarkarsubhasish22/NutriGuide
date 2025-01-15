package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

public class POILogFactory {
    private static String _loggerClassName = null;
    private static Map _loggers = new HashMap();
    private static POILogger _nullLogger = new NullLogger();

    private POILogFactory() {
    }

    public static POILogger getLogger(Class theclass) {
        return getLogger(theclass.getName());
    }

    public static POILogger getLogger(String cat) {
        POILogger logger;
        if (_loggerClassName == null) {
            try {
                _loggerClassName = System.getProperty("org.apache.poi.util.POILogger");
            } catch (Exception e) {
            }
            if (_loggerClassName == null) {
                _loggerClassName = _nullLogger.getClass().getName();
            }
        }
        if (_loggerClassName.equals(_nullLogger.getClass().getName())) {
            return _nullLogger;
        }
        if (_loggers.containsKey(cat)) {
            return (POILogger) _loggers.get(cat);
        }
        try {
            logger = (POILogger) Class.forName(_loggerClassName).newInstance();
            logger.initialize(cat);
        } catch (Exception e2) {
            logger = _nullLogger;
        }
        _loggers.put(cat, logger);
        return logger;
    }
}
