package com.y0ngb1n.boot.lombok.example;

import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * 再也不用写那些差不多的 LOG 啦
 *
 * @see lombok.extern.apachecommons.CommonsLog  @CommonsLog
 * @see lombok.extern.java.Log                  @Log
 * @see lombok.extern.log4j.Log4j               @Log4j
 * @see lombok.extern.log4j.Log4j2              @Log4j2
 * @see lombok.extern.slf4j.Slf4j               @Slf4j
 * @see lombok.extern.slf4j.XSlf4j              @XSlf4j
 * @see lombok.extern.jbosslog.JBossLog         @JBossLog
 * @see lombok.extern.flogger.Flogger           @Flogger
 */
@Log
public class _15_LogExample {

  public static void main(String... args) {
    log.severe("Something's wrong here");
  }
}

@Slf4j
class _15_LogExampleOther {

  public static void main(String... args) {
    log.error("Something else is wrong here");
  }
}

@CommonsLog(topic = "CounterLog")
class _15_LogExampleCategory {

  public static void main(String... args) {
    log.error("Calling the 'CounterLog' with a message");
  }
}

//  翻译成 Java 程序是：
//public class _15_LogExample {
//  private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(_15_LogExample.class.getName());
//
//  public static void main(String... args) {
//    log.severe("Something's wrong here");
//  }
//}
//
//public class _15_LogExampleOther {
//  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(_15_LogExampleOther.class);
//
//  public static void main(String... args) {
//    log.error("Something else is wrong here");
//  }
//}
//
//public class _15_LogExampleCategory {
//  private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("CounterLog");
//
//  public static void main(String... args) {
//    log.error("Calling the 'CounterLog' with a message");
//  }
//}