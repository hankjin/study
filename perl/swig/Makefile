all:
	swig -perl example.i
	cc -fPIC -c example.c
	cc -fPIC -c example_wrap.c `perl -MExtUtils::Embed -e ccopts`
	ld -fPIC -shared example.o example_wrap.o -o example.so

clean:
	rm -fr *.so
	rm -fr *.o
	rm -fr *.pm
