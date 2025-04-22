package demoMod.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

public class MyPreDexTransform extends Transform {

    Project project
    // 添加构造，为了方便从plugin中拿到project对象，待会有用
    public MyPreDexTransform(Project project) {
        this.project = project
        def libPath = project.project(':hack').buildDir.absolutePath.concat("\\intermediates\\javac\\debug\\compileDebugJavaWithJavac\\classes")
        Inject.appendClassPath(libPath)
        libPath = project.project(':android').buildDir.absolutePath.concat("\\intermediates\\javac\\debug\\compileDebugJavaWithJavac\\classes")
        Inject.appendClassPath(libPath)

        Inject.appendClassPath(project.project(':android').rootDir.absolutePath.concat("\\android\\lib\\desktop-1.0.jar"))

        Inject.appendClassPath(project.project(':hack').buildDir.absolutePath.concat("\\intermediates\\javac\\release\\classes"))
        Inject.appendClassPath(project.project(':android').buildDir.absolutePath.concat("\\intermediates\\javac\\release\\classes"))

        Inject.appendClassPath("C:\\Users\\gaoming\\AppData\\Local\\Android\\Sdk\\platforms\\android-30\\android.jar")
        Inject.appendClassPath("\"C:\\Users\\gaoming\\Downloads\\gradle-7.0.2-bin\\gradle-7.0.2\\init.d\\caches\\modules-2\\files-2.1\\com.badlogicgames.gdx\\gdx\\1.9.5\\cd5eec61aacab35347e28eb026de358c7e83536f\\target\\gdx-1.9.5.jar\"")
    }

    // Transfrom在Task列表中的名字
    // TransfromClassesWithPreDexForXXXX
    @Override
    String getName() {
        return "preDex"
    }

    // 指定input的类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transfrom的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        // inputs就是输入文件的集合
        // outputProvider可以获取outputs的路径
        project.logger.error "================开始注入代码！=========="
        inputs.each {TransformInput input ->

            input.directoryInputs.each {DirectoryInput directoryInput->
                project.logger.error "扫描：" + directoryInput.file.absolutePath
                //代码注入
                Inject.injectDir(directoryInput.file.absolutePath)

                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each {JarInput jarInput->
                project.logger.error "扫描：" + jarInput.file.absolutePath
                //代码注入
                String jarPath = jarInput.file.absolutePath
                //String projectName = project.rootProject.name
                /*
                if(jarPath.endsWith("classes.jar")
                        && jarPath.contains("exploded-aar\\"+projectName)
                        // hotpatch module是用来加载dex，无需注入代码
                        && !jarPath.contains("exploded-aar\\"+projectName+"\\hotpatch")) {

                }
                */
                Inject.injectJar(jarPath)
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                String bakName = "desktop-1.0.jar.bak"
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0,jarName.length()-4)
                }
                def dest = outputProvider.getContentLocation(jarName+md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                println "dest output file path:" + dest.getAbsolutePath()
                FileUtils.copyFile(jarInput.file, dest)

                def bakFile = new File(jarInput.file.getParentFile().getAbsolutePath() + File.separator + bakName)
                if (bakFile.exists()) {
                    jarInput.file.delete()
                    FileUtils.copyFile(bakFile, jarInput.file)
                    bakFile.delete()
                }
            }
        }
    }
}