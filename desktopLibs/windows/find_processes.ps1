$OutputEncoding = [console]::InputEncoding = [console]::OutputEncoding = New-Object System.Text.UTF8Encoding;
$processes = Get-Process
foreach ($process in $processes) {
    $process.Name;
}