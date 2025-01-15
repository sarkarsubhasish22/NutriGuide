package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsLogger extends POILogger {
    private static LogFactory _creator = LogFactory.getFactory();
    private Log log = null;

    public void initialize(String cat) {
        this.log = _creator.getInstance(cat);
    }

    public void log(int level, Object obj1) {
        if (level == FATAL) {
            if (this.log.isFatalEnabled()) {
                this.log.fatal(obj1);
            }
        } else if (level == ERROR) {
            if (this.log.isErrorEnabled()) {
                this.log.error(obj1);
            }
        } else if (level == WARN) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(obj1);
            }
        } else if (level == INFO) {
            if (this.log.isInfoEnabled()) {
                this.log.info(obj1);
            }
        } else if (level == DEBUG) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(obj1);
            }
        } else if (this.log.isTraceEnabled()) {
            this.log.trace(obj1);
        }
    }

    public void log(int level, Object obj1, Throwable exception) {
        if (level == FATAL) {
            if (!this.log.isFatalEnabled()) {
                return;
            }
            if (obj1 != null) {
                this.log.fatal(obj1, exception);
            } else {
                this.log.fatal(exception);
            }
        } else if (level == ERROR) {
            if (!this.log.isErrorEnabled()) {
                return;
            }
            if (obj1 != null) {
                this.log.error(obj1, exception);
            } else {
                this.log.error(exception);
            }
        } else if (level == WARN) {
            if (!this.log.isWarnEnabled()) {
                return;
            }
            if (obj1 != null) {
                this.log.warn(obj1, exception);
            } else {
                this.log.warn(exception);
            }
        } else if (level == INFO) {
            if (!this.log.isInfoEnabled()) {
                return;
            }
            if (obj1 != null) {
                this.log.info(obj1, exception);
            } else {
                this.log.info(exception);
            }
        } else if (level == DEBUG) {
            if (!this.log.isDebugEnabled()) {
                return;
            }
            if (obj1 != null) {
                this.log.debug(obj1, exception);
            } else {
                this.log.debug(exception);
            }
        } else if (!this.log.isTraceEnabled()) {
        } else {
            if (obj1 != null) {
                this.log.trace(obj1, exception);
            } else {
                this.log.trace(exception);
            }
        }
    }

    public boolean check(int level) {
        if (level == FATAL) {
            if (this.log.isFatalEnabled()) {
                return true;
            }
            return false;
        } else if (level == ERROR) {
            if (this.log.isErrorEnabled()) {
                return true;
            }
            return false;
        } else if (level == WARN) {
            if (this.log.isWarnEnabled()) {
                return true;
            }
            return false;
        } else if (level == INFO) {
            if (this.log.isInfoEnabled()) {
                return true;
            }
            return false;
        } else if (level != DEBUG || !this.log.isDebugEnabled()) {
            return false;
        } else {
            return true;
        }
    }
}
