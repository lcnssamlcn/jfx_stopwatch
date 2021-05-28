@echo off


mkdir dist
call ant jar
call ant run-win_x64
