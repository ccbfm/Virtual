package com.virtual.mutual.simple.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.virtual.mutual.simple.launcher.model.App;
import com.virtual.util.context.VContextHolder;
import com.virtual.util.persist.sp.VSp;
import com.virtual.util.thread.VThread;
import com.virtual.util.thread.model.VSimpleTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AppLoad {

    private AppLoad() {
    }

    private static final class Singleton {
        private static final AppLoad INSTANCE = new AppLoad();
    }

    public static AppLoad instance() {
        return Singleton.INSTANCE;
    }

    private static final String SP_NAME = "simple_launcher";
    private final HashMap<String, App> mWorkspaces = new HashMap<>();
    private final LinkedList<App> mWorkspaceApps = new LinkedList<>();
    private final LinkedList<App> mAllApps = new LinkedList<>();
    private @LoadStatus int mLoadStatus = LoadStatus.NONE;

    private @interface LoadStatus {
        int NONE = 0;
        int LOADING = 1;
        int LOADED = 2;
    }

    public void load(Context context) {
        if (mLoadStatus != LoadStatus.NONE) {
            return;
        }
        mLoadStatus = LoadStatus.LOADING;

        mAllApps.clear();
        mWorkspaces.clear();
        mWorkspaceApps.clear();

        Set<String> workspace_package_names = VSp.get(context, SP_NAME).getStringSet("workspace_package_names");
        if (workspace_package_names != null) {
            for (String name : workspace_package_names) {
                mWorkspaces.put(name, new App());
            }
        }
        VThread.execute(VThread.getCpuPool(), new VSimpleTask<List<App>>() {
            @Override
            protected List<App> doTask() throws Throwable {
                Context context = VContextHolder.instance().getContext();
                PackageManager pm = context.getPackageManager();
                Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> resolveInfoList;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    resolveInfoList =
                            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0));
                } else {
                    resolveInfoList = pm.queryIntentActivities(intent, 0);
                }
                if (resolveInfoList != null) {
                    for (ResolveInfo info : resolveInfoList) {
                        CharSequence label = info.loadLabel(pm);
                        Drawable icon = info.loadIcon(pm);
                        String packageName = info.activityInfo.packageName;

                        Intent intentT = new Intent();
                        intentT.setComponent(new ComponentName(packageName, info.activityInfo.name));
                        intentT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        boolean hasWorkplace = false;
                        App wApp = mWorkspaces.get(packageName);
                        if (wApp != null) {
                            wApp.name = label;
                            wApp.icon = icon;
                            wApp.packageName = packageName;
                            wApp.intent = intentT;
                            hasWorkplace = true;
                        }
                        App app = new App(label, icon, packageName, intentT);
                        app.hasWorkplace = hasWorkplace;
                        mAllApps.add(app);
                    }
                    mWorkspaceApps.addAll(mWorkspaces.values());
                    return mAllApps;
                }
                return null;
            }

            @Override
            protected void onSuccess(List<App> result) {
                mLoadStatus = result != null ? LoadStatus.LOADED : LoadStatus.NONE;
                Log.d("AppLoad", "mAllApps " + mAllApps.size() + " mWorkspaceApps " + mWorkspaceApps.size());
                if (mWorkspaceCallback != null) {
                    mWorkspaceCallback.change(mWorkspaceApps);
                }
                if (mAllAppsCallback != null) {
                    mAllAppsCallback.change(mAllApps);
                }
            }
        }.setMainResultHandler());
    }

    private Callback mWorkspaceCallback, mAllAppsCallback;

    public void setWorkspaceCallback(Callback workspaceCallback) {
        mWorkspaceCallback = workspaceCallback;
        if (mLoadStatus == LoadStatus.LOADED) {
            workspaceCallback.change(mWorkspaceApps);
        }
    }

    public void setAllAppsCallback(Callback allAppsCallback) {
        mAllAppsCallback = allAppsCallback;
        if (mLoadStatus == LoadStatus.LOADED) {
            allAppsCallback.change(mAllApps);
        }
    }

    public void addWorkspaceApp(Context context, App app) {
        App wApp = new App(app);
        app.hasWorkplace = true;
        mWorkspaces.put(wApp.packageName, wApp);
        mWorkspaceApps.add(wApp);
        if (mWorkspaceCallback != null) {
            mWorkspaceCallback.change(mWorkspaceApps);
        }
        VSp.get(context, SP_NAME).putStringSet("workspace_package_names", mWorkspaces.keySet());
    }

    public void removeWorkspaceApp(Context context, App app) {
        App wApp = mWorkspaces.remove(app.packageName);
        if (wApp != null) {
            app.hasWorkplace = false;
            mWorkspaceApps.remove(wApp);
        }
        if (mWorkspaceCallback != null) {
            mWorkspaceCallback.change(mWorkspaceApps);
        }
        VSp.get(context, SP_NAME).putStringSet("workspace_package_names", mWorkspaces.keySet());
    }

    public void searchApps(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            if (mAllAppsCallback != null) {
                mAllAppsCallback.change(mAllApps);
            }
        } else {
            List<App> apps = new LinkedList<>();
            for (App app : mAllApps) {
                if (app.name != null && app.name.toString().contains(keyword)) {
                    apps.add(app);
                }
            }
            if (mAllAppsCallback != null) {
                mAllAppsCallback.change(apps);
            }
        }
    }

    public interface Callback {
        void change(List<App> apps);
    }
}
