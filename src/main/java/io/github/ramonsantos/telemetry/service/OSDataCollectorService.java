package io.github.ramonsantos.telemetry.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class OSDataCollectorService {

    private static final int OS_INFO = 1;
    private static final int MEMORY_INFO = 2;
    private static final int FREE_DISK_INFO = 3;
    private static final int USED_DISK_INFO = 4;

    public String getUptime() throws IOException {

        int SEC_IN_DAY = 86400;

        String uptimeOut = this.getOutOSCommand("cat /proc/uptime");
        uptimeOut = uptimeOut.substring(0, uptimeOut.indexOf('.'));

        int timeIn = Integer.parseInt(uptimeOut);

        int secOfDay = timeIn % SEC_IN_DAY;
        int days = timeIn / SEC_IN_DAY;

        LocalTime timeOfDay = LocalTime.ofSecondOfDay(secOfDay);
        String time = timeOfDay.toString();

        return (days + ":" + time);

    }

    public String getKernelInfo() throws IOException {

        return this.getOutOSCommand("uname -o -m -v -r").trim();

    }

    public String getOSInfo() throws IOException {

        String osInfo = this.getOutScript(OS_INFO);
        osInfo = osInfo.replaceAll("PRETTY_NAME=", "").replaceAll("\"", "");

        return osInfo;

    }

    public Map<String, String> getMemoryInfo() throws IOException {

        String[] values = this.getOutScript(MEMORY_INFO).replaceAll("\n", "").split(" ");

        Map<String, String> result = new HashMap<String, String>();

        result.put("total", values[0]);
        result.put("used", values[1]);
        result.put("free", values[2]);
        result.put("buff/cache", values[3]);

        return result;
    }

    public Map<String, String> getDiskInfo() throws IOException {


        Map<String, String> result = new HashMap<String, String>();



        Integer freeSpace = this.getSpaceDisk(FREE_DISK_INFO);
        Integer usedSpace = this.getSpaceDisk(USED_DISK_INFO);

        result.put("total", freeSpace.intValue() + usedSpace.intValue() + "");
        result.put("used", usedSpace.toString());
        result.put("free", freeSpace.toString());

        return result;

    }

    private Integer getSpaceDisk(Integer option) throws IOException {

        Integer disk = 0;

        String out = this.getOutScript(option);

        String array[] = out.split("\n");

        for (int i = 0; i < array.length; i++) {

            disk = disk + Integer.parseInt(array[i]);

        }

        return disk;

    }

    private String getPathScript() {

        return getClass().getResource("/data_collector.sh").getPath();

    }

    private String getOutScript(int option) throws IOException {

        String command = "bash " + this.getPathScript() + " " + option;

        return this.getOutOSCommand(command);

    }

    private String getOutOSCommand(String command) throws IOException {

        StringBuilder commandOut = new StringBuilder();

        Process child = Runtime.getRuntime().exec(command);

        InputStream in = child.getInputStream();
        int c;

        while ((c = in.read()) != -1) {

            commandOut.append((char) c);

        }

        in.close();

        return commandOut.toString();

    }

}
