SWIG usage
====

# Pre requirements
```
sudo apt-get install swig
sudo apt-get install libperl-dev
```

# Steps
```
swig -perl example.i
cc -fPIC -c example.c
cc -fPIC -c example_wrap.c `perl -MExtUtils::Embed -e ccopts`
ld -fPIC -shared example.o example_wrap.o -o example.so
perl hello.pl
```

# Diagnose
* fatal error: EXTERN.h: No such file or directory
  * add EXTERN.h include path with `perl -MExtUtils::Embed -e ccopts` ` 
* relocation R_X86_64_32S against `.rodata' can not be used when making a shared object; recompile with -fPIC
  * add -fPIC option for compile of .c files 
