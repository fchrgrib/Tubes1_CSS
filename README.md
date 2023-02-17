# Tugas Besar 1 IF2211 Strategi Algoritma
> Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan “Galaxio”


## Tentang Program
Galaxio adalah game battle royale yang mempertandingkan beberapa bot kapal. Tujuan dari permainan adalah agar bot kapal tetap hidup hingga akhir permainan. Agar dapat memenangkan pertandingan,  diimplementasikan seatu strategi, yaitu strategi greedy untuk memenangkan permainan.


## Strategi Greedy
Strategi yang kita terapkan pada permainan ini, implementasinya kurang lebih adalah melakukan aksi untuk tetap bertahan dan melakukan penyerangan. Algoritma greedy yang telah dibuat memiliki urutan prioritas sebagai berikut:
    1. Teleport ke musuh yang lebih kecil
    2. Menggunakan shield jika terdapat FIRETORPEDO dari musuh
    3. Menggunkan FIRETORPEDO jita terdapat musuh dalam radius tertentu
    4. Mengejar musuh yang lebih kecil 
    5. Memakan makanan yang tidak dekat dengan objek yang berbahaya


## Requirements
Untuk dapat menjalankan permainan ini, maka pastikan perangkat sudah dilengkapi oleh aplikasi berikut :
1. [Bahasa Pemrograman Java (minimal Java 11)](https://www.oracle.com/java/technologies/downloads/#java)
2. [IDE Intellij](https://www.jetbrains.com/idea/) / [VS Code](https://code.visualstudio.com/download) + [Maven](https://maven.apache.org/download.cgi) 
3. [.Net Core 3.1: Link tersedia pada panduan](https://docs.google.com/document/d/1Ym2KomFPLIG_KAbm3A0bnhw4_XQAsOKzpTa70IgnLNU/edit#)


## Cara Menjalankan Program
1. [Download file starter-pack.zip](https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2)
2. [Unzip file pada mesin eksekusi]
3. Clone repository ini ke dalam folder starter-pack
4. Buka file "run.bat" lalu tuliskan path pada java.jar untuk menjalankan game, lalu di save
5. Buka kembali file yang bernama "run.bat" pada folder starter-pack atau ketik perintah ./run pada command prompt dan permainan akan secara otomatis berjalan.
6. Riwayat permainan akan muncul pada folder logger-publish.


## Struktur Program
tubes1_STIMA                                               
├─ src                                                     
│  └─ main                                                 
│     └─ java                                              
│        ├─ Enums                                          
│        │  ├─ ObjectTypes.java                            
│        │  └─ PlayerActions.java                          
│        ├─ Models                                         
│        │  ├─ GameObject.java                             
│        │  ├─ GameState.java                              
│        │  ├─ GameStateDto.java                           
│        │  ├─ PlayerAction.java                           
│        │  ├─ Position.java                               
│        │  └─ World.java                                  
│        ├─ Services                                       
│        │  ├─ BotService.java                             
│        │  └─ pseudocode                                  
│        └─ Main.java                                      
├─ target                                                  
│  ├─ classes                                              
│  │  ├─ Enums                                             
│  │  │  ├─ ObjectTypes.class                              
│  │  │  └─ PlayerActions.class                            
│  │  ├─ Models                                            
│  │  │  ├─ GameObject.class                               
│  │  │  ├─ GameState.class                                
│  │  │  ├─ GameStateDto.class                             
│  │  │  ├─ PlayerAction.class                             
│  │  │  ├─ Position.class                                 
│  │  │  └─ World.class                                    
│  │  ├─ Services                                          
│  │  │  ├─ BotService.class                               
│  │  │  └─ pseudocode                                     
│  │  └─ Main.class                                        
│  ├─ libs                                                 
│  │  ├─ azure-core-1.13.0.jar                             
│  │  ├─ gson-2.8.5.jar                                    
│  │  ├─ jackson-annotations-2.11.3.jar                    
│  │  ├─ jackson-core-2.11.3.jar                           
│  │  ├─ jackson-databind-2.11.3.jar                       
│  │  ├─ jackson-dataformat-xml-2.11.3.jar                 
│  │  ├─ jackson-datatype-jsr310-2.11.3.jar                
│  │  ├─ jackson-module-jaxb-annotations-2.11.3.jar        
│  │  ├─ jakarta.activation-api-1.2.1.jar                  
│  │  ├─ jakarta.xml.bind-api-2.3.2.jar                    
│  │  ├─ netty-tcnative-boringssl-static-2.0.35.Final.jar  
│  │  ├─ okhttp-3.11.0.jar                                 
│  │  ├─ okio-1.14.0.jar                                   
│  │  ├─ reactive-streams-1.0.2.jar                        
│  │  ├─ reactor-core-3.3.12.RELEASE.jar                   
│  │  ├─ rxjava-2.2.2.jar                                  
│  │  ├─ signalr-1.0.0.jar                                 
│  │  ├─ slf4j-api-1.7.25.jar                              
│  │  ├─ slf4j-simple-1.7.25.jar                           
│  │  ├─ stax2-api-4.2.1.jar                               
│  │  └─ woodstox-core-6.2.1.jar                           
│  ├─ maven-archiver                                       
│  │  └─ pom.properties                                    
│  ├─ maven-status                                         
│  │  └─ maven-compiler-plugin                             
│  │     └─ compile                                        
│  │        └─ default-compile                             
│  │           ├─ createdFiles.lst                         
│  │           └─ inputFiles.lst                           
│  ├─ test-classes                                         
│  ├─ JavaBot.jar                                          
│  ├─ JavaBot2.jar                                         
│  └─ JavaBotJason.jar                                     
├─ Dockerfile                                              
├─ JavaBot.iml                                             
├─ pom.xml                                                 
└─ README.md                                               

                                              
## Technologies Used
- Java


## Author
```
13521028 - M Zulfiansyah Bayu Pratama	
13521030 - Jauza Lathifah Annassalafi
13521031 - Fahrian Afdholi
```

