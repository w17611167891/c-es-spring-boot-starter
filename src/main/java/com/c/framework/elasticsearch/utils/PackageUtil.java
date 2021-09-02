package com.c.framework.elasticsearch.utils;


import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;


/**
 * 通过接口获取所有实现
 *
 * @author W.C
 */
public class PackageUtil {

    /**
     * 查询包下所有接口
     * @param packageName
     * @return
     */
    public static List<Class> findInPackageInter(String packageName){
        return ExUtil.exceptionHandler("根据包名获取包下所有接口:",()->{
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            List<Class> returnClassList = new ArrayList<>();
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(packageName) + "/**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerfactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                if(clazz.isInterface()) returnClassList.add(clazz);
            }
            return returnClassList;
        });
    }

    /**
     * 获取包下所有接口
     *
     * @param packagePath
     * @return
     */
    public static List<Class> getAllClassByPackage(String packagePath) {
        ArrayList<Class> returnClassList = new ArrayList<>();
        try {
            List<Class> allClass = getClasses(packagePath);
            // 判断是否是一个接口
            for (int i = 0; i < allClass.size(); i++) {
                if (allClass.get(i).isInterface())
                    returnClassList.add(allClass.get(i));
            }
        } catch (Exception e) {
        }
        return returnClassList;
    }

    /**
     * 获取包下所有类
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    /**
     * 获取类
     * @param directory
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Class> findClass(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}