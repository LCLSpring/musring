package com.fivemonth.musring;

import com.fivemonth.musring.resourceresolver.ResourceResolver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sub.AnnoScan;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceResolverTest {

    @Test
    public void scanClass(){
        var pkg = "com.fivemonth.musring.scan";
        var rr = new ResourceResolver(pkg);
        List<String> classes = rr.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").
                        replace("\\", ".");
            }
            return null;
        });
        Collections.sort(classes);
        System.out.println(classes);
        String[] newClasses = new String[]{
                "com.fivemonth.musring.scan.convert.ValueConverterBean",
                "com.fivemonth.musring.scan.destroy.AnnotationDestroyBean",
                "com.fivemonth.musring.scan.destroy.SpecifyDestroyBean",
                "com.fivemonth.musring.scan.destroy.SpecifyDestroyConfiguration",
                "com.fivemonth.musring.scan.init.AnnotationInitBean",
                "com.fivemonth.musring.scan.init.SpecifyInitBean",
                "com.fivemonth.musring.scan.init.SpecifyInitConfiguration",
                "com.fivemonth.musring.scan.nested.OuterBean",
                "com.fivemonth.musring.scan.proxy.FirstProxyBean",
                "com.fivemonth.musring.scan.proxy.FirstProxyBeanPostProcessor",
                "com.fivemonth.musring.scan.proxy.OriginBean",
                "com.fivemonth.musring.scan.proxy.SecondProxyBean",
                "com.fivemonth.musring.scan.proxy.SecondProxyBeanPostProcessor",
                "com.fivemonth.musring.scan.sub1.sub2.Sub2Bean",
                "com.fivemonth.musring.scan.sub1.Sub1Bean",
                "com.fivemonth.musring.scan.sub1.sub2.sub3.Sub3Bean"
        };
        for (String ins : newClasses){
            assertTrue(classes.contains(ins));
        }
    }

    @Test
    public void scanJar(){
        var pkg = PostConstruct.class.getPackageName();
        var rr = new ResourceResolver(pkg);
        List<String> classes = rr.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").
                        replace("\\", ".");
            }
            return null;
        });
        // classes in jar:
        assertTrue(classes.contains(PostConstruct.class.getName()));
        assertTrue(classes.contains(PreDestroy.class.getName()));
        assertTrue(classes.contains(PermitAll.class.getName()));
        assertTrue(classes.contains(DataSourceDefinition.class.getName()));
        // jakarta.annotation.sub.AnnoScan is defined in classes:
        assertTrue(classes.contains(AnnoScan.class.getName()));
    }

    @Test
    public void scanText(){
        var pkg = "com.fivemonth.musring.scan";
        var rr = new ResourceResolver(pkg);
        List<String> txts = rr.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".txt")) {
                return name.replace("\\", "/");
            }
            return null;
        });
        Collections.sort(txts);
        assertArrayEquals(new String[]{
                "com/fivemonth/musring/scan/sub1/sub1/sub1.txt",
                "com/fivemonth/musring/scan/sub1/sub1/sub2/sub2.txt",
                "com/fivemonth/musring/scan/sub1/sub1/sub2/sub3/sub3.txt",
        },txts.toArray(String[]::new));
    }
}
