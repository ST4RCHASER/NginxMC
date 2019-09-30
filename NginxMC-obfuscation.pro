-injars out\artifacts\NginxMC_Master\NginxMC_Master.jar
-outjars out\artifacts\NginxMC_Master\NginxMC.jar

-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\charsets.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\deploy.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\access-bridge-64.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\cldrdata.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\dnsns.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\jaccess.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\jfxrt.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\localedata.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\nashorn.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\sunec.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\sunjce_provider.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\sunmscapi.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\sunpkcs11.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext\zipfs.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\javaws.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\jce.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\jfr.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\jfxswt.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\jsse.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\management-agent.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\plugin.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\resources.jar'
-libraryjars 'C:\Program Files\Java\jdk1.8.0_201\jre\lib\rt.jar'
-libraryjars 'I:\work\java_project\all_jars\ProtocolLib 4.4.0.jar'
-libraryjars 'I:\work\java_project\Bukkit_Spigot_Bungeecord\BungeeCord.jar'
-libraryjars 'I:\work\java_project\all_jars\PlaceholderAPI-2.9.2.jar'
-libraryjars 'I:\work\java_project\all_jars\Vault.jar'
-libraryjars 'I:\work\java_project\all_jars\NametagEdit.jar'
-libraryjars 'I:\work\java_project\all_jars\HolographicDisplays.jar'
-libraryjars 'I:\work\java_project\Bukkit_Spigot_Bungeecord\spigot18.jar'

-dontshrink
-dontoptimize
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleAnnotations,RuntimeInvisibleParameterAnnotations
-ignorewarnings



-keep,allowshrinking public class me.starchaser.nginxmc.bungee.core extends net.md_5.bungee.api.plugin.Plugin {
    public void onEnable();
    public void onDisable();
}

-keep,allowshrinking public class me.starchaser.nginxmc.bukkit.core extends org.bukkit.plugin.java.JavaPlugin {
    public void onEnable();
    public void onDisable();
}

-keep,allowshrinking class me.starchaser.nginxmc.api.* {
    <fields>;
    <methods>;
}

-keepattributes InnerClasses
 -keep class me.starchaser.nginxmc.bukkit.NginxPlayer**
 -keepclassmembers class me.starchaser.nginxmc.bukkit.NginxPlayer** {
    *;
 }
 -keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}