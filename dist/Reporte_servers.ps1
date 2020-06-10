$result = @()
$ServerList = Get-Content "C:\Users\mayro\Desktop\servers.txt"
$CurrTime = (get-date) - (new-timespan -day 30)
foreach ($i in $ServerList){
Get-WinEvent -FilterHashtable @{LogName="Security";ID=4732;StartTime=$CurrTime} -ComputerName $i| Foreach {
$event = [xml]$_.ToXml()
if($event)
{
$CurrTime = Get-Date $_.TimeCreated -UFormat "%Y-%d-%m %H:%M:%S"
$New_GrpUser = $event.Event.EventData.Data[0]."#text"
$AD_Group = $event.Event.EventData.Data[2]."#text"
$sid = $event.Event.EventData.Data[1]."#text"
try {
    $prueba2 = ((New-Object System.Security.Principal.SecurityIdentifier($sid)).Translate( [System.Security.Principal.NTAccount])).Value
} catch {
    $prueba2 = Get-WmiObject -Query "Select * from Win32_UserAccount Where SID = '$sid' " -ComputerName $i | Select-Object -ExpandProperty Name 
}
$AdminWhoAdded = $event.Event.EventData.Data[6]."#text"
$dc = $event.Event.System.computer
$dc + “|” + $CurrTime + “|” + $AD_Group + “|” + $New_GrpUser + “|” + $AdminWhoAdded + "|"  + $prueba2 
$result += $dc + “,” + $CurrTime + “,” + $AD_Group + “,” + $New_GrpUser + “,” + $AdminWhoAdded + ","  + $prueba2
}
}
}

$result | out-file -filepath C:\so.txt -append -width 200