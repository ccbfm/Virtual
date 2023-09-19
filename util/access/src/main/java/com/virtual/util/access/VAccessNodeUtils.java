package com.virtual.util.access;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;

public class VAccessNodeUtils {
    private static final String TAG = "AccessNodeUtils";

    public static boolean boundsInScreen(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            return rect.left >= 0 && rect.top >= 0 && rect.right > 0 && rect.bottom > 0;
        }
        return false;
    }

    public static boolean contains(String target, CharSequence text) {
        if (target == null || text == null) {
            return false;
        }
        return text.toString().contains(target);
    }

    public static boolean contains(String[] targets, CharSequence text) {
        if (targets == null || text == null) {
            return false;
        }
        for (String str : targets) {
            if (contains(str, text)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equalsPackageName(AccessibilityNodeInfo root, @NonNull String packageName) {
        if (root == null) {
            return false;
        }
        if (root.getPackageName() == null) {
            return false;
        }
        return packageName.contentEquals(root.getPackageName());
    }

    public static List<AccessibilityNodeInfo> findByViewId(AccessibilityNodeInfo root, @NonNull String idStr) {
        if (root == null) {
            return null;
        }
        return root.findAccessibilityNodeInfosByViewId(idStr);
    }

    public static AccessibilityNodeInfo findByViewId(AccessibilityNodeInfo root, @NonNull String idStr, int index) {
        List<AccessibilityNodeInfo> infoList = findByViewId(root, idStr);
        if (infoList != null) {
            int size = infoList.size();
            if (index < size) {
                return infoList.get(index);
            }
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findByText(AccessibilityNodeInfo root, @NonNull String text) {
        if (root == null) {
            return null;
        }
        return root.findAccessibilityNodeInfosByText(text);
    }

    public static AccessibilityNodeInfo findByText(AccessibilityNodeInfo root, @NonNull String text, int index) {
        List<AccessibilityNodeInfo> infoList = findByText(root, text);
        if (infoList != null) {
            int size = infoList.size();
            if (index < size) {
                return infoList.get(index);
            }
        }
        return null;
    }

    public static boolean clickByNode(AccessibilityService service, AccessibilityNodeInfo nodeInfo) {
        if (service == null || nodeInfo == null) {
            return false;
        }

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;

        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
        GestureDescription gesture = builder.build();

        return service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i(TAG, "clickByNode onCompleted " + nodeInfo);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i(TAG, "clickByNode onCancelled " + nodeInfo);
            }
        }, null);
    }

    public static boolean scrollVerticalByNode(AccessibilityService service, AccessibilityNodeInfo nodeInfo, boolean up) {
        if (service == null || nodeInfo == null) {
            return false;
        }

        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        int x = (rect.left + rect.right) / 2;
        int y = (rect.top + rect.bottom) / 2;
        int w4 = w >> 2;
        int h4 = h >> 2;
        int h16 = h >> 4;

        Path path = new Path();
        if (up) {
            path.moveTo(randomValue(x, w4), randomValue(y + h4, h16));
            path.lineTo(randomValue(x, w4), randomValue(y - h4, h16));
        } else {
            path.moveTo(randomValue(x, w4), randomValue(y - h4, h16));
            path.lineTo(randomValue(x, w4), randomValue(y + h4, h16));
        }
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
        GestureDescription gesture = builder.build();

        return service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i(TAG, "clickByNode onCompleted " + nodeInfo);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i(TAG, "clickByNode onCancelled " + nodeInfo);
            }
        }, null);
    }


    private static Random sRandom;

    private static int randomValue(int ox, int range) {
        if (sRandom == null) {
            sRandom = new Random();
        }
        Random random = sRandom;
        int flag = random.nextInt(2);
        int tx = random.nextInt(range);
        return flag == 0 ? ox + tx : ox - tx;
    }
}
