
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

java -cp out BSDSAssignment1_dongxu_han.CADiagnoseClient localhost 1000 News Sports Junk &

DiagPID="$!"
#echo "Diagnose Client PID=$DiagPID"
trap "kill $ServPID $DiagPID" SIGINT SIGTERM 

java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 1 Pub#1 News 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 1 Pub#2 Sports 10000 &
java -cp out BSDSAssignment1_dongxu_han.CAPubClient localhost 1 Pub#3 Junk 10000 &

sleep 5
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost News &
Sub1PID="$!"
java -cp out BSDSAssignment1_dongxu_han.CASubClient localhost Sports &
Sub2PID="$!"
trap "kill $ServPID $DiagPID $Sub1PID $Sub2PID " SIGINT SIGTERM
wait
