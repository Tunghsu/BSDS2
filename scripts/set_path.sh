cd ../../
CWD_SRC=/src/main/java/
if [ ! -n $CLASSPATH ];
then
	export CLASSPATH=$CLASSPATH:"$PWD$CWD_SRC"
else 
	export CLASSPATH="$PWD$CWD_SRC"
fi
echo CLASSPATH=$CLASSPATH
