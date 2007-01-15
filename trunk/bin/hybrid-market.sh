#!/bin/bash

WORKING_DIRECTORY=`pwd`

PREFIX=$HOME/workspace/hybrid-vm

HYBRID_VM_LIB=$PREFIX/lib

CLASSPATH=$HYBRID_VM_LIB/hybrid-vm-market-0.2.jar:$HYBRID_VM_LIB/simjava.jar:$HYBRID_VM_LIB/log4j-1.2.14.jar:$HYBRID_VM_LIB/junit-4.1.jar

java -cp $CLASSPATH org.mundau.market.Main $*

