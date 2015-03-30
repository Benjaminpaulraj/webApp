package attendanceclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceThread extends Thread {

//    protected static MainWindow mainWindow = new MainWindow();
    protected static boolean startSync = false;
    private static int requestCount = 0;
    private boolean isStopped = true;
    public static final Logger LOGGER = Logger.getLogger("AttendanceThread");

    public boolean isRunning() {
        return !isStopped;
    }

    public void startSync() {
        LOGGER.log(Level.INFO, "\n\n\n\n\n\n\n  start sync called==>>" + isStopped + "====" + this.isAlive());
        requestCount = 0;
        isStopped = false;
        if (this.isAlive()) {
            return;
        }
        
        if(!ConfigInfo.getTimeZone().equals("")){
            TimeZone.setDefault(TimeZone.getTimeZone(ConfigInfo.getTimeZone()));
        }
        this.start();
    }

    public void stopSync() {
        isStopped = true;
    }

    public static void resetCount() {
        requestCount = 0;
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO, "\n\n\n\n\n\n\n  thread called " + isStopped + "=====" + requestCount);

        while (!isStopped) {
            LogFile.configLogger();  
            try {
                String dateformat = "yyyy-MM-dd HH:mm:ss";

                Date now = new Date();
                String ftime = new SimpleDateFormat(dateformat).format(new Date(ConfigInfo.getLastRequestTime()));
                String ttime = new SimpleDateFormat(dateformat).format(now.getTime());

                ConfigInfo.logString= new StringBuilder();
                ConfigInfo.logString.append(ttime);
                ConfigInfo.logString.append("\t").append(ftime);
                ConfigInfo.logString.append("\t").append(ttime);
                ZPAServerRequest.sendArrayHTTPRequest(SQLAccess.readFromMSSQLServer(ftime, ttime), now);

                ConfigInfo.writeFile();
                LogFile.LOGGER.log(Level.INFO,ConfigInfo.logString.toString());
                
                LOGGER.log(Level.INFO,"\n valueee"+ConfigInfo.reqSuccessCount+"");
                LOGGER.log(Level.INFO,"\n\n\n\n\n config val\n\n\n====>>>>"+ConfigInfo.config);

                
                long sleepTime = ConfigInfo.getSleepTime();
                
                LOGGER.log(Level.INFO,"\n\n\n\n\n gonna sleep\n\n\n====>>>>"+sleepTime);
                AttendanceThread.sleep(sleepTime);
                
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
