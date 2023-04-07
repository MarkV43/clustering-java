set  shell := ["powershell.exe"]

alias r := run

run *ARGS:
    gradle run --stacktrace --args='{{ARGS}}'