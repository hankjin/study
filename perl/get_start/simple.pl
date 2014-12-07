#!/usr/bin/perl

# data types
print 100_100_100, "\n";

# quote q<sep> qq<sep>
print '\'hello\n\\ joke', "\n";
print q/hello worlp' this is john/;
print qq/welcome here\n/;
print qq|welcome here\n|;
print qq}welcome here\n};

# Here-Documents
print <<EOF
This is a here-document. It starts on the line after the two arrows,
and it ends when the text following the arrow is found at the beginning
of a line, like this:

EOF
;

# convert 
print hex("0x30"), "\n"; #48
print oct('030'), "\n";  #24
print hex('030'), "\n";  #48
print oct('17'), "\n";   #15
print oct('18'), "\n";   #1

# operator
print 6&4, "\n";    # 4
print 6|4, "\n";    # 6
print ~6, "\n";     # 18446744073709551609
print 4 != 6, "\n"; # 1
print 8 << 2, "\n"; # 1**2
print 8 >> 2, "\n"; # 
print "hello "."world"."\n";
# string multiply
print "hello "x(4*3), "\n";
# string to number
print "12 monkeys" + 0, "\n"; # 12
print "monkeys 12" + 0, "\n"; # 0
print "A=".ord('a'), "\n";    # 97
print "four" eq "six", "\n";  # 0 eq is string compare
print "four" == "six", "\n";  # 1 == is number compare

# variable
$name = "Hank";
print "My name is ", $name, "\n";
print '$_=', $_, "\n";

# input
print 'What is your name:';
$name = <STDIN>;
print "Hello [$name]\n";
