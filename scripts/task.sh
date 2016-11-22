
#source set_path.sh
#cd -
#cd ../
#rm -rf out/*.class
#javac -d out src/main/java/org/dongxu/CAClient/*.java
#java -cp out BSDSAssignment1_dongxu_han.CAServer &
#ServPID="$!"
#echo "ServPID=$PID"

EndingChar=" "
Disable="/dev/null"

if [ $# -gt 0 ];
then
    EndingChar="> $Disable"
    echo $EndingChar
fi

#java -cp out BSDSAssignment1_dongxu_han.CADiagnoseClient localhost 1000 News Sports &

#DiagPID="$!"
#echo "Diagnose Client PID=$DiagPID"
#trap "kill $ServPID " SIGINT SIGTERM 

java -cp "../target/classes:../lib/*" org.dongxu.CAClient.CAPubClient localhost 1 Pub1 News 10000 $EndingChar &
java -cp "../target/classes:../lib/*" org.dongxu.CAClient.CAPubClient localhost 1 Pub2 Sports 20000 $EndingChar &

sleep 5
java -cp "../target/classes:../lib/*" org.dongxu.CAClient.CASubClient localhost News $EndingChar &
Sub1PID="$!"
java -cp "../target/classes:../lib/*" org.dongxu.CAClient.CASubClient localhost Sports $EndingChar &
Sub2PID="$!"
trap "kill $Sub1PID $Sub2PID " SIGINT SIGTERM
wait
