
source set_path.sh
cd -
cd ../
rm -rf out/*.class
javac -d out ./*.java
ADDR=localhost

sleep 1

if [ $# -gt 0 ];
then
	ADDR=$1
	echo $ADDR
fi


java -cp out BSDSAssignment1_dongxu_han.CADiagnoseClient $ADDR 1000 News Sports Weather Politics Movies Society Food Celebrity Finance Music &

DiagPID="$!"
#echo "Diagnose Client PID=$DiagPID"
trap "kill $DiagPID" SIGINT SIGTERM 

java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#1 News 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#2 Sports 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#3 Weather 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#4 Politics 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#5 Movies 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#6 Food 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#7 Society 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#8 Celebrity 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#9 Music 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient $ADDR 2 Pub#10 Finance 10000 &

java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR News &
Sub1PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Sports &
Sub2PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Weather &
Sub3PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Politics &
Sub4PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Movies &
Sub5PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Food &
Sub6PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Society &
Sub7PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Celebrity &
Sub8PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Music &
Sub9PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient $ADDR Finance &
Sub10PID="$!"
trap "kill $ServPID $DiagPID $Sub1PID $Sub2PID $Sub3PID $Sub4PID $Sub5PID $Sub6PID $Sub7PID $Sub8PID $Sub9PID $Sub10PID" SIGINT SIGTERM
wait
