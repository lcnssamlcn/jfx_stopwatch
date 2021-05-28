@echo off


mkdir dist
call ant jar
make -f Makefile.win clean
make -f Makefile.win
