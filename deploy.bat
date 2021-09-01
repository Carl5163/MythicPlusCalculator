@RD /S /Q "build/"
echo "Deleted build/ folder"
call .\gradlew vaadinBuildFrontend bootJar
echo "Generating jar file"

set ec2="ec2-34-234-90-122.compute-1.amazonaws.com"

echo "Copying Script"
call scp -i "../mythicplus.pem" execute_commands_on_ec2.sh ec2-user@%ec2%:/home/ec2-user

echo "Copying Jar"
call scp -i "../mythicplus.pem" build/libs/mythicpluscalculator-0.0.1-SNAPSHOT.jar ec2-user@%ec2%:/home/ec2-user

echo "Starting Jar"
call ssh -i "../mythicplus.pem" ec2-user@%ec2% chmod 777 execute_commands_on_ec2.sh
call ssh -i "../mythicplus.pem" ec2-user@%ec2% ./execute_commands_on_ec2.sh
