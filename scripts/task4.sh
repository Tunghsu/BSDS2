
source set_path.sh
cd -
cd ../
rm -rf out/*.class
javac -d out ./*.java
java -cp out BSDSAssignment1_dongxu_han.CAServer &
ServPID="$!"
#echo "ServPID=$PID"

echo "Server should have started ..."
sleep 1

java -cp out BSDSAssignment1_dongxu_han.CADiagnoseClient localhost 1000 News Sports Weather Politics Movies Society Food Celebrity Finance Music &

DiagPID="$!"
#echo "Diagnose Client PID=$DiagPID"
trap "kill $ServPID $DiagPID" SIGINT SIGTERM 

java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#1 News 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#2 Sports 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#3 Weather 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#4 Politics 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#5 Movies 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#6 Food 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#7 Society 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#8 Celebrity 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#9 Music 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 2 Pub#10 Finance 10000 &

sleep 5

java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost News &
Sub1PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Sports &
Sub2PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Weather &
Sub3PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Politics &
Sub4PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Movies &
Sub5PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Food &
Sub6PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Society &
Sub7PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Celebrity &
Sub8PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Music &
Sub9PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Finance &
Sub10PID="$!"
trap "kill $ServPID $DiagPID $Sub1PID $Sub2PID $Sub3PID $Sub4PID $Sub5PID $Sub6PID $Sub7PID $Sub8PID $Sub9PID $Sub10PID" SIGINT SIGTERM
wait
