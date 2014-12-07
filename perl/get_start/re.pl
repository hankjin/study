#!/usr/bin/perl
#
use strict;
use warnings;

my $str = "hello hank john";
if ($str =~ "john") {
    print "john is found\n";
}
if ($str !~ "john") {
    print "john not found\n";
}

$_ = "Hello Hank John";
if (/Hank/) {
    print "Hank is Found\n";
}
