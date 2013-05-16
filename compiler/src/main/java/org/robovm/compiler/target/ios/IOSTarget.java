/*
 * Copyright (C) 2012 Trillian AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.target.ios;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.exec.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.OS;
import org.robovm.compiler.log.DebugOutputStream;
import org.robovm.compiler.log.ErrorOutputStream;
import org.robovm.compiler.target.AbstractTarget;
import org.robovm.compiler.target.LaunchParameters;
import org.robovm.compiler.util.Executor;
import org.robovm.compiler.util.io.OpenOnWriteFileOutputStream;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Transient;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;


/**
 * @author niklas
 *
 */
public class IOSTarget extends AbstractTarget {

    @Element(required = false)
    private Arch arch;
    @Transient
    private SDK sdk;
    @Element(name = "sdk")
    private String sdkVersion;
    @Element(required = false)
    private File infoPList = null;
    @Transient
    private NSDictionary infoPListDict = null;
    @Element(required = false)
    private File resourceRulesPList;
    @Element(required = false)
    private File entitlementsPList;
    @Element(required = false)
    private String signIdentity = "iPhone Developer";
    
    public IOSTarget() {
    }
    
    @Override
    public Arch getArch() {
        return arch;
    }
    
    public void setArch(Arch arch) {
        if (arch != Arch.x86 && arch != Arch.thumbv7) {
            throw new IllegalArgumentException("Arch '" + arch + "' is unsupported for iOS target");
        }
        this.arch = arch;
    }
    
    @Override
    public LaunchParameters createLaunchParameters() {
        if (arch == Arch.x86) {
            return new IOSSimulatorLaunchParameters();
        }
        return super.createLaunchParameters();
    }
    
    public List<SDK> getSDKs() {
        if (arch == Arch.x86) {
            return SDK.listSimulatorSDKs();
        } else {
            return SDK.listDeviceSDKs();
        }
    }

    @Override
    protected Executor createExecutor(LaunchParameters launchParameters)
            throws IOException {
        
        if (arch == Arch.x86) {
            return createIOSSimExecutor(launchParameters);
        } else {
            return createFruitstrapExecutor(launchParameters);
        }
    }
    
    private Executor createIOSSimExecutor(LaunchParameters launchParameters)
            throws IOException {

        File dir = getAppDir();
        
        String iosSimPath = new File(config.getHome().getBinDir(), "ios-sim").getAbsolutePath();
        
        List<Object> args = new ArrayList<Object>();
        args.add("launch");
        args.add(dir);
        args.add("--unbuffered");
        if (((IOSSimulatorLaunchParameters) launchParameters).getSdk() != null) {
            args.add("--sdk");
            args.add(((IOSSimulatorLaunchParameters) launchParameters).getSdk());
        }
        args.add("--family");
        args.add(((IOSSimulatorLaunchParameters) launchParameters).getFamily().toString().toLowerCase());
        if (launchParameters.getStdoutFifo() != null) {
            args.add("--stdout");
            args.add(launchParameters.getStdoutFifo());
        }
        if (launchParameters.getStderrFifo() != null) {
            args.add("--stderr");
            args.add(launchParameters.getStderrFifo());
        }
        if (!launchParameters.getArguments().isEmpty()) {
            args.add("--args");
            args.addAll(launchParameters.getArguments());
        }
        
        return super.createExecutor(launchParameters, iosSimPath).args(args);
    }
    

    @SuppressWarnings("resource")
    private Executor createFruitstrapExecutor(LaunchParameters launchParameters)
            throws IOException {

        File dir = getAppDir();
        
        String fruitstrapPath = new File(config.getHome().getBinDir(), "fruitstrap").getAbsolutePath();
        
        List<Object> args = new ArrayList<Object>();
        args.add("--verbose");
        args.add("--unbuffered");
        args.add("--debug");
        args.add("--gdbargs");
        args.add("-i mi -q");
        args.add("--nostart");
        
        if (!launchParameters.getArguments().isEmpty()) {
            args.add("--args");
            args.add(joinArgs(launchParameters.getArguments()));
        }

        args.add("--bundle");
        args.add(dir.getAbsolutePath());
        
        OutputStream fruitstrapOut = new DebugOutputStream(config.getLogger());
        OutputStream fruitstrapErr = new ErrorOutputStream(config.getLogger());
        OutputStream out = null;
        OutputStream err = null;
        if (launchParameters.getStdoutFifo() != null) {
            out = new OpenOnWriteFileOutputStream(launchParameters.getStdoutFifo());
        } else {
            out = System.out;
        }
        if (launchParameters.getStderrFifo() != null) {
            err = new OpenOnWriteFileOutputStream(launchParameters.getStderrFifo());
        } else {
            err = System.err;
        }
        
        return super.createExecutor(launchParameters, fruitstrapPath)
                .args(args)
                .streamHandler(new FruitstrapStreamHandler(out, err, fruitstrapOut, fruitstrapErr));
    }
    
    private String joinArgs(List<String> args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(StringUtils.quoteArgument(arg));
        }
        return sb.toString();
    }
    
    @Override
    protected void doBuild(File outFile, List<String> ccArgs,
            List<File> objectFiles, List<String> libArgs)
            throws IOException {

        ccArgs.add("-miphoneos-version-min=3.0");
        ccArgs.add("-isysroot");
        ccArgs.add(sdk.getRoot().getAbsolutePath());
        super.doBuild(outFile, ccArgs, objectFiles, libArgs);
    }

    protected void prepareInstall(File installDir) throws IOException {
        createInfoPList(installDir);
        generateDsym(installDir, getExecutable());
        if (arch == Arch.thumbv7) {
            copyResourcesPList(installDir);
            codesign(signIdentity, entitlementsPList, installDir);
        }
    }
    
    protected void prepareLaunch(File appDir) throws IOException {
        super.doInstall(appDir, getExecutable());
        createInfoPList(appDir);
        generateDsym(appDir, getExecutable());
        if (arch == Arch.thumbv7) {
            copyResourcesPList(appDir);
            codesign(signIdentity, getEntitlementsPList(), appDir);
        }
    }
    
    private void codesign(String identity, File entitlementsPList, File appDir) throws IOException {
        List<Object> args = new ArrayList<Object>();
        args.add("-f");
        args.add("-s");
        args.add(identity);
        if (entitlementsPList != null) {
            args.add("--entitlements");
            args.add(entitlementsPList);
        }
        args.add(appDir);
        new Executor(config.getLogger(), "codesign")
            .addEnv("CODESIGN_ALLOCATE", 
                new Executor(config.getLogger(), "xcrun")
                    .args("-sdk", "iphoneos", "-f", "codesign_allocate")
                    .execCapture())
            .args(args)
            .exec();
    }
    
    private void copyResourcesPList(File destDir) throws IOException {
        File destFile = new File(destDir, "ResourceRules.plist");
        if (resourceRulesPList != null) {
            FileUtils.copyFile(resourceRulesPList, destFile);
        } else {
            FileUtils.copyURLToFile(getClass().getResource("/ResourceRules.plist"), destFile);
        }
    }
    
    private File getEntitlementsPList() throws IOException {
        try {
            File destFile = new File(config.getTmpDir(), "Entitlements.plist");
            if (entitlementsPList != null) {
                NSDictionary dict = (NSDictionary) PropertyListParser.parse(entitlementsPList);
                dict.put("get-task-allow", true);
                PropertyListParser.saveAsXML(dict, destFile);
            } else {
                FileUtils.copyURLToFile(getClass().getResource("/Entitlements.plist"), destFile);
            }
            return destFile;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void generateDsym(File dir, String executable) throws IOException {
        File dsymDir = new File(dir.getParentFile(), dir.getName() + ".dSYM");
        FileUtils.deleteDirectory(dsymDir);
        new Executor(config.getLogger(), "xcrun")
            .args("dsymutil", "-o", dsymDir, new File(dir, executable))
            .exec();
    }

    @Override
    protected void doInstall(File installDir, String executable) throws IOException {
        super.doInstall(installDir, getExecutable());
        prepareInstall(installDir);
    }
    
    @Override
    protected Process doLaunch(LaunchParameters launchParameters) throws IOException {
        prepareLaunch(getAppDir());
        return super.doLaunch(launchParameters);
    }

    protected File getAppDir() {
        File dir = null;
        if (!config.isSkipInstall()) {
            dir = config.getInstallDir();
        } else {
            dir = new File(config.getTmpDir(), getExecutable() + ".app");
            dir.mkdirs();
        }
        return dir;
    }
    
    protected String getExecutable() {
        if (infoPListDict != null) {
            NSObject bundleExecutable = infoPListDict.objectForKey("CFBundleExecutable");
            if (bundleExecutable != null) {
                return bundleExecutable.toString();
            }
        }
        return config.getExecutableName();
    }

    protected void customizeInfoPList(NSDictionary dict) {
        if (arch == Arch.x86) {
            dict.put("CFBundleSupportedPlatforms", new NSArray(new NSString("iPhoneSimulator")));
        } else {
            dict.put("CFBundleResourceSpecification", "ResourceRules.plist");
            dict.put("CFBundleSupportedPlatforms", new NSArray(new NSString("iPhoneOS")));
        }
    }
    
    protected void createInfoPList(File dir) throws IOException {
        NSDictionary dict = new NSDictionary();
        if (infoPListDict != null) {
            for (String key : infoPListDict.allKeys()) {
                dict.put(key, infoPListDict.objectForKey(key));
            }
        } else {
            dict.put("CFBundleExecutable", config.getExecutableName());
            dict.put("CFBundleName", config.getExecutableName());
            String identifier = config.getMainClass() != null ? config.getMainClass() : config.getExecutableName();
            dict.put("CFBundleIdentifier", identifier);
            dict.put("CFBundlePackageType", "APPL");
            dict.put("LSRequiresIPhoneOS", true);
            if (sdk.getDefaultProperty("SUPPORTED_DEVICE_FAMILIES") != null) {
                // Values in SUPPORTED_DEVICE_FAMILIES are NSStrings while UIDeviceFamily
                // values should be NSNumbers.
                NSArray defFamilies = (NSArray) sdk.getDefaultProperty("SUPPORTED_DEVICE_FAMILIES");
                NSArray families = new NSArray(defFamilies.count());
                for (int i = 0; i < families.count(); i++) {
                    families.setValue(i, new NSNumber(defFamilies.objectAtIndex(i).toString()));
                }
                dict.put("UIDeviceFamily", families);
            }
            dict.put("UISupportedInterfaceOrientations", new NSArray(
                    new NSString("UIInterfaceOrientationPortrait"),
                    new NSString("UIInterfaceOrientationLandscapeLeft"),
                    new NSString("UIInterfaceOrientationLandscapeRight"),
                    new NSString("UIInterfaceOrientationPortraitUpsideDown")
            ));
            dict.put("UISupportedInterfaceOrientations~ipad", new NSArray(
                    new NSString("UIInterfaceOrientationPortrait"),
                    new NSString("UIInterfaceOrientationLandscapeLeft"),
                    new NSString("UIInterfaceOrientationLandscapeRight"),
                    new NSString("UIInterfaceOrientationPortraitUpsideDown")
            ));
            dict.put("UIRequiredDeviceCapabilities", new NSArray(new NSString("armv7")));
        }

        dict.put("DTPlatformName", sdk.getDefaultProperty("PLATFORM_NAME"));
        dict.put("DTPlatformVersion", sdk.getVersion());
        dict.put("DTSDKName", sdk.getCanonicalName());
        
        customizeInfoPList(dict);

        File tmpInfoPlist = new File(config.getTmpDir(), "Info.plist");
        PropertyListParser.saveAsBinary(dict, tmpInfoPlist);
        
        config.getLogger().debug("Installing Info.plist to %s", dir);
        FileUtils.copyFile(tmpInfoPlist, new File(dir, tmpInfoPlist.getName()));
    }
    
    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
    
    public void setInfoPList(File infoPList) {
        this.infoPList = infoPList;
    }
    
    public void init(Config config) {
        super.init(config);
        if (arch == null) {
            arch = Arch.thumbv7;
        }
        if (this.infoPList != null) {
            try {
                this.infoPListDict = (NSDictionary) PropertyListParser.parse(this.infoPList);
            } catch (Throwable t) {
                throw new IllegalArgumentException(t);
            }
        }
        List<SDK> sdks = getSDKs();
        if (this.sdkVersion == null) {
            Collections.sort(sdks);
            this.sdk = sdks.get(sdks.size() - 1);
        } else {
            for (SDK sdk : sdks) {
                if (sdk.getVersion().equals(sdkVersion)) {
                    this.sdk = sdk;
                    break;
                }
            }
            if (sdk == null) {
                throw new IllegalArgumentException("No SDK found matching version string " + sdkVersion);
            }
        }
    }

    @Override
    public OS getOS() {
        return OS.ios;
    }

    @Override
    public boolean canLaunchInPlace() {
        return false;
    }

    public void setResourceRulesPList(File resourceRulesPList) {
        this.resourceRulesPList = resourceRulesPList;
    }

    public void setEntitlementsPList(File entitlementsPList) {
        this.entitlementsPList = entitlementsPList;
    }

    public void setSignIdentity(String signIdentity) {
        this.signIdentity = signIdentity;
    }
}
