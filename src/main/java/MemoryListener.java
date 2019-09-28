import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;

import com.sun.management.GarbageCollectionNotificationInfo;
import java.lang.management.MemoryNotificationInfo;

public class MemoryListener {

    public static void BindListeners(){
        for (GarbageCollectorMXBean gcMbean : ManagementFactory.getGarbageCollectorMXBeans()) {
            try {
                ManagementFactory.getPlatformMBeanServer().
                        addNotificationListener(gcMbean.getObjectName(), listenerGC, null,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        
        // for (MemoryPoolMXBean memBean : ManagementFactory.getMemoryPoolMXBeans()) {
        //     try {
        //         // https://stackoverflow.com/questions/2057792/garbage-collection-notification
        //         memBean.setUsageThreshold(1);
        //         ManagementFactory.getPlatformMBeanServer().
        //                 addNotificationListener(memBean.getObjectName(), listenerMem, null,null);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }
    }

     // influence:
    // https://stackoverflow.com/questions/28495280/how-to-detect-a-long-gc-from-within-a-jvm
    // https://www.programcreek.com/java-api-examples/?code=vitaly-chibrikov/otus_java_2017_06/otus_java_2017_06-master/L2.2-gc-subscribe/src/main/java/ru/otus/l21/Main.java
    public static NotificationListener listenerGC = new NotificationListener() {
        @Override
        public void handleNotification(Notification notification, Object handback) {
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                long duration = info.getGcInfo().getDuration();
                String gctype = info.getGcAction();

                System.out.println("GarbageCollection: "+
                    info.getGcAction() + " " +
                    info.getGcName() +
                    " duration: " + info.getGcInfo().getDuration() + "ms" +
                    " used: " + sumUsedMb(info.getGcInfo().getMemoryUsageBeforeGc()) + "MB" +
                    " -> " + sumUsedMb(info.getGcInfo().getMemoryUsageAfterGc()) + "MB");
            }
        }
    };

    public static NotificationListener listenerMem = new NotificationListener(){
        @Override
        // https://docs.oracle.com/javase/7/docs/api/java/lang/management/MemoryPoolMXBean.html
        public void handleNotification(Notification notification, Object handback) {
            if(notification.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                CompositeData cd = (CompositeData) notification.getUserData();
                MemoryNotificationInfo info = MemoryNotificationInfo.from(cd);

                System.out.println("MemBean: \n" +
                    "\tpoolName: " + info.getPoolName() +
                    "\tcount: " + info.getCount() +
                    "\tusage: " + info.getUsage()
                );
            }
        }
    };

    public static long sumUsedMb(Map<String, MemoryUsage> memUsages) {
        long sum = 0;
        for (MemoryUsage memoryUsage : memUsages.values()) {
            sum += memoryUsage.getUsed();
        }
        return sum / (1024 * 1024);
    }
}