package khanzahmsanjungan;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

public class MoveWindowExample {
    static HWND hwnda;
    
    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        
        int GetLastError();
    }

    public static void main(String[] args) {
        User32 u32 = User32.INSTANCE;
        u32.EnumWindows((WinDef.HWND hwnd, Pointer pntr) -> {
            char[] windowText = new char[512];
            u32.GetWindowText(hwnd, windowText, 512);
            String wText = Native.toString(windowText);

            if (wText.toLowerCase().contains("face recognition bpjs kesehatan")) {
                hwnda = hwnd;
                return false;
            }

            return true;
        }, Pointer.NULL);
        
        if (hwnda != null) {
            boolean result = u32.SetWindowPos(hwnda, null, 100, 100, 800, 600, 0);
            if (!result) {
                System.out.println("Error: " + Kernel32.INSTANCE.GetLastError());
            }
        } else {
            System.out.println("Window not found.");
        }
    }
}