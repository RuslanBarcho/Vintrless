$OutputEncoding = [console]::InputEncoding = [console]::OutputEncoding = New-Object System.Text.UTF8Encoding;
Get-ChildItem -Path "C:\ProgramData\Microsoft\Windows\Start Menu" -Recurse -Filter *.lnk | Foreach-Object {
    $baseName = $_.Basename;

    if (-Not ($baseName -Match "install")) {
        $extension = $_.Extension;
        $path = $_.FullName;

        $sh = New-Object -ComObject WScript.Shell;
        $shortcut = $sh.CreateShortcut($path);

        $targetPath = $shortcut.TargetPath;
        if ($targetPath) {
            $targetPath = $targetPath.Replace("\", "\\");
        }

        $targetArgs = $shortcut.Arguments;
        if ($targetArgs) {
            $targetArgs = $targetArgs.Replace("\", "\\").Replace("`"", "\`"");
        }

        $targetWorkingDirectory = $shortcut.WorkingDirectory;
        if ($targetWorkingDirectory) {
            $targetWorkingDirectory = $targetWorkingDirectory.Replace("\", "\\");
        }

        if ((-Not ($targetPath -Match "install")) -And ($targetPath -Match ".exe") -And (-Not ($targetPath -Match "unins000"))) {
            "`{ `"name`": `"" + $baseName + "`", `"location`": `"" + $targetPath + "`", `"arguments`": `"" + $targetArgs + "`", `"workingDirectory`": `"" + $targetWorkingDirectory + "`" }";
        }
    }
}

$AppDataPath = $env:APPDATA;
$StartMenuPath = $AppDataPath + "\Microsoft\Windows\Start Menu\Programs";

Get-ChildItem -Path $StartMenuPath -Recurse -Filter *.lnk | Foreach-Object {
    $baseName = $_.Basename;

    if (-Not ($baseName -Match "install")) {
        $extension = $_.Extension;
        $path = $_.FullName;

        $sh = New-Object -ComObject WScript.Shell;
        $shortcut = $sh.CreateShortcut($path);

        $targetPath = $shortcut.TargetPath;
        if ($targetPath) {
            $targetPath = $targetPath.Replace("\", "\\");
        }

        $targetArgs = $shortcut.Arguments;
        if ($targetArgs) {
            $targetArgs = $targetArgs.Replace("\", "\\").Replace("`"", "\`"");
        }

        $targetWorkingDirectory = $shortcut.WorkingDirectory;
        if ($targetWorkingDirectory) {
            $targetWorkingDirectory = $targetWorkingDirectory.Replace("\", "\\");
        }

        if ((-Not ($targetPath -Match "install")) -And ($targetPath -Match ".exe") -And (-Not ($targetPath -Match "unins000"))) {
            "`{ `"name`": `"" + $baseName + "`", `"location`": `"" + $targetPath + "`", `"arguments`": `"" + $targetArgs + "`", `"workingDirectory`": `"" + $targetWorkingDirectory + "`" }";
        }
    }
}