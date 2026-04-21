param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArguments
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
& "$scriptDir\mvnw.cmd" @MavenArguments
exit $LASTEXITCODE

