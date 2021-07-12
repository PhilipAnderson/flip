EESchema Schematic File Version 5
EELAYER 30 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
Comment5 ""
Comment6 ""
Comment7 ""
Comment8 ""
Comment9 ""
$EndDescr
Connection ~ 6400 2350
Connection ~ 6950 2350
Connection ~ 7500 2350
Connection ~ 8000 2350
Connection ~ 8500 2350
Connection ~ 9000 2350
Wire Wire Line
	5800 2150 5800 2350
Wire Wire Line
	6400 2350 6400 2150
Wire Wire Line
	6950 2350 6950 2150
Wire Wire Line
	7500 2350 7500 2150
Wire Wire Line
	8000 2350 8000 2150
Wire Wire Line
	8500 2350 8500 2150
Wire Wire Line
	9000 2350 9000 2650
Wire Wire Line
	8500 1850 8500 1500
Wire Wire Line
	8000 1850 8000 1400
Wire Wire Line
	7500 2350 8000 2350
Wire Wire Line
	8000 2350 8500 2350
Wire Wire Line
	8500 1500 9000 1500
Wire Wire Line
	8500 2350 9000 2350
Wire Wire Line
	6400 2350 6950 2350
Wire Wire Line
	6950 2350 7500 2350
Wire Wire Line
	7500 1850 7500 1300
Wire Wire Line
	5800 2350 6400 2350
Wire Wire Line
	6950 1850 6950 1200
Wire Wire Line
	6400 1850 6400 1100
Wire Wire Line
	9000 2350 9000 1600
Wire Wire Line
	5800 1850 5800 1000
Wire Wire Line
	8000 1400 9000 1400
Wire Wire Line
	7500 1300 9000 1300
Wire Wire Line
	6950 1200 9000 1200
Wire Wire Line
	6400 1100 9000 1100
Wire Wire Line
	5800 1000 9000 1000
$Comp
L power:GND #PWR?
U 1 1 5DD533D4
P 9000 2650
F 0 "#PWR?" H 9000 2400 50  0001 C CNN
F 1 "GND" H 9005 2477 50  0000 C CNN
F 2 "" H 9000 2650 50  0001 C CNN
F 3 "" H 9000 2650 50  0001 C CNN
	1    9000 2650
	1    0    0    -1  
$EndComp
$Comp
L Device:R R1
U 1 1 5DD49985
P 5800 2000
F 0 "R1" H 5870 2046 50  0000 L CNN
F 1 "R" H 5870 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 5730 2000 50  0001 C CNN
F 3 "~" H 5800 2000 50  0001 C CNN
	1    5800 2000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R2
U 1 1 5DD49C0A
P 6400 2000
F 0 "R2" H 6470 2046 50  0000 L CNN
F 1 "R" H 6470 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 6330 2000 50  0001 C CNN
F 3 "~" H 6400 2000 50  0001 C CNN
	1    6400 2000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R3
U 1 1 5DD4A22F
P 6950 2000
F 0 "R3" H 7020 2046 50  0000 L CNN
F 1 "R" H 7020 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 6880 2000 50  0001 C CNN
F 3 "~" H 6950 2000 50  0001 C CNN
	1    6950 2000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R4
U 1 1 5DD4A49C
P 7500 2000
F 0 "R4" H 7570 2046 50  0000 L CNN
F 1 "R" H 7570 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 7430 2000 50  0001 C CNN
F 3 "~" H 7500 2000 50  0001 C CNN
	1    7500 2000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R5
U 1 1 5DD4A9AD
P 8000 2000
F 0 "R5" H 8070 2046 50  0000 L CNN
F 1 "R" H 8070 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 7930 2000 50  0001 C CNN
F 3 "~" H 8000 2000 50  0001 C CNN
	1    8000 2000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R6
U 1 1 5DD4AF12
P 8500 2000
F 0 "R6" H 8570 2046 50  0000 L CNN
F 1 "R" H 8570 1955 50  0000 L CNN
F 2 "custom-library:FSR-402" V 8430 2000 50  0001 C CNN
F 3 "~" H 8500 2000 50  0001 C CNN
	1    8500 2000
	1    0    0    -1  
$EndComp
$Comp
L Connector_Generic:Conn_01x07 J1
U 1 1 5DD4E3E8
P 9200 1300
F 0 "J1" H 9280 1342 50  0000 L CNN
F 1 "Conn_01x07" H 9280 1251 50  0000 L CNN
F 2 "Connector_PinHeader_2.54mm:PinHeader_1x07_P2.54mm_Horizontal" H 9200 1300 50  0001 C CNN
F 3 "~" H 9200 1300 50  0001 C CNN
	1    9200 1300
	1    0    0    -1  
$EndComp
$Comp
L Connector:DB9_Male J?
U 1 1 5E5C8FB8
P 10250 1850
F 0 "J?" H 10430 1895 50  0000 L CNN
F 1 "DB9_Male" H 10430 1805 50  0000 L CNN
F 2 "" H 10250 1850 50  0001 C CNN
F 3 " ~" H 10250 1850 50  0001 C CNN
	1    10250 1850
	1    0    0    -1  
$EndComp
$EndSCHEMATC