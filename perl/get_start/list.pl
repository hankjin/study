#!/usr/bin/perl
use warnings;
use strict;

print (123, 456, 789, "\n");
print qw(123 456 789),  "\n";
print (123, (456, 789), "\n");
print (123, qw(456 789), "\n");
print (q(123), (456, 789), "\n");
print ((123, 456)[1], "\n");     # 456

print qw(
    January February    March
    April   May         June
    July    August      September
    October November    December
    )[3], "\n";
print ((0, 10,20,30,40,50)[0, 2, 4], "\n");

print ((1 .. 6), "\n");
print ((6 .. 1), "\n");
print (('a' .. 'z'), "\n");
print (('x' .. 'p'), "\n"); # xyz
print (reverse('a' .. 'z'), "\n");

# array variable use @ instead of $
my $name1 = (1 .. 6);
my @name2 = (1 .. 6);
my $name3 = "@name2";
print $name1, "\n";     # no output
print @name2, "\n";     # 123456
print $name3, "\n";     # 1 2 3 4 5 6

print ((1 .. 20)[3 .. 6], "\n");

# iterate array
my @week = qw(Monday Tuesday Wednesday Thursday Friday Saturday Sunday);
for (0 .. $#week) {
    print $week[$_], "\n";
}

# push and pop
my @desk = ("pen", "pencil");
print "@desk\n";
push @desk, "ballpen";
print "desk=@desk\n";
my $bpen = pop @desk;
print "poped=$bpen\n";
# unshift and shift opposite of push pop
unshift @desk, "notebook";
print "unshift notebook=@desk\n";
my $book = shift @desk;
print "shift=$book result=@desk\n";
unshift @desk, "note", "book";

my @sort_desk = sort @desk;
print "desk=@desk sorted=@sort_desk\n";

# hash with %
my @array = qw(Gary Dallas Lucy Exeter Ian Reading);
my %where = @array;
print "array=@array hash=",
print %where;
print "\n";
print $where{"Gary"}, "\n";
# keys, $xx{}
for (keys %where) {
    print "$_ lives in $where{$_}\n";
}


