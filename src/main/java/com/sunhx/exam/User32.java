package com.sunhx.exam;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

interface User32 extends StdCallLibrary {
    User32 INSTANCE = (User32)Native.load("user32", User32.class);
    boolean SystemParametersInfoA(int uiAction, int uiParam, String pvParam, int fWinIni);
    int MessageBoxA(int a, String b, String c, int d);
}