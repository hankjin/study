#!/usr/bin/perl
use strict;
use warnings;

my ($a, $b);
$a = 8;
if (defined $a) {
    print "A defined\n";
}
else {
    print "A undefined\n";
}
if (defined $b) {
    print "B defined\n";
}
else {
    print "B undefined\n";
}

if (3 == 3) {
    print "3 == 3\n";
}
if ("hi" eq "hi") {
    print "hi eq hi\n";
}
if ("hi" ne "hello") {
    print "hi ne hello\n";
}

my %where = (
    Hank => "John",
);
# if elsif else
if (exists $where{"Hank"}){
    print "exists where{Hank}\n";
}
my @array = qw(8 7 5 6);
my $i;
for $i (@array) {
    print "i=$i\n";
}
foreach $i (@array) {
    print "i=$i\n";
}
foreach (@array) {
    print "seq=$_\n";
}
my $total = 0;
$total += $_ for @array;
print "total=$total\n";

# while
# last=break next=continue
while (<STDIN>) {
    print "Input=$_\n";
    last;
}
