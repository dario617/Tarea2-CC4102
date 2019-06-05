import re
import argparse
import os
largos = [2**i for i in range(10,24)]

def cleanAlphaNumeric(filename: str, newfilepreffix: str, limit: int):
    with open(filename, 'r', encoding='cp1252') as source:
        with open(filename+"_clean_"+newfilepreffix, 'w') as dest:
            line = source.readline()
            chars = 0
            newlines = re.compile('[\t\n\r\f\v]+')
            space = re.compile(' \w*')
            while(line and chars < limit):
                if(newlines.fullmatch(line)):
                    line = source.readline()
                    continue
                #Limpiamos signos de puntuación y saltos de línea con " "
                clean0 = re.sub('[\t\n\r\f\v]', ' ', line).lower()
                #Acotamos el alfabeto a a-z, 0-9 y " "
                clean1 = re.sub(r'[^a-z0-9 ]+', ' ', clean0)
                #Limpiamos los espacios múltiples y los reemplazamos con simples
                clean2 = re.sub(' +', ' ', clean1)
                #Si un string comienza con " ", lo sacamos
                if(space.match(clean2)):
                    clean2 = clean2[1:]
                if(chars + len(clean2) >= limit):
                    clean2 = clean2[:limit - chars]
                chars += len(clean2)
                dest.write(clean2)
                line = source.readline()
            print(f"Texto con {chars} caracteres generado!")
            dest.close()
            source.close()

def cleanNewLine(filename: str, newfilepreffix: str, limit: int):
    with open(filename, 'r', encoding='cp1252') as source:
        with open(filename+"_clean_"+newfilepreffix, 'w') as dest:
            line = source.readline()
            chars = 0
            newlines = re.compile('[\t\n\r\f\v]+')
            while(line and chars < limit):
                if(newlines.fullmatch(line)):
                    line = source.readline()
                    continue
                #Limpiamos saltos de linea y espacios
                clean0 = re.sub('[\s]', '', line).lower()
                if(chars + len(clean0) >= limit):
                    clean0 = clean0[:limit - chars]
                chars += len(clean0)
                dest.write(clean0)
                line = source.readline()
            print(f"Texto con {chars} caracteres generado!")
            dest.close()
            source.close()

def knowYourUniverse(filename: str):
    universe = set()
    with open(filename, 'r', encoding = 'ascii') as source:
        line = source.readline()
        while(line):
            universe = universe.union(set(line))
            line = source.readline()
    print(universe)

def generateTexts(which: int):
    if which == 0:
        for i, largo in enumerate(largos):
            preffix = "2^"+str(i+10)
            print(preffix)
            cleanAlphaNumeric("english.50MB", preffix, largo)
    else:
        for i, largo in enumerate(largos):
            preffix = "2^"+str(i+10)
            print(preffix)
            cleanNewLine("dna.50MB", preffix, largo)


parser = argparse.ArgumentParser(description='Limpio textos porque puedo')
parser.add_argument('palabra', metavar='p', type=str,
                    help='dna genera los dna, eng los english, ambos genera ambos (?)\
                    rmdna borra los dna, rmeng borra los eng y rmall los borra todillos')
args = parser.parse_args()
pref = ["2^"+str(i) for i in range(10,24)]
if(args.palabra == "dna"):
    generateTexts(1)
elif(args.palabra == "eng"):
    generateTexts(0)
elif(args.palabra == "both"):
    generateTexts(1)
    generateTexts(0)
elif(args.palabra == "rmdna"):
    for pr in pref:
        try:
            os.remove("dna.50MB_clean_"+pr)
        except:
            print("El dna no estaba!")
elif(args.palabra == "rmdeng"):
    for pr in pref:
        try:
            os.remove("english.50MB_clean_"+pr)
        except:
            print("el english no estaba!")
elif(args.palabra == "rmall"):
    for pr in pref:
        try:
            os.remove("dna.50MB_clean_"+pr)
        except:
            print("El dna no estaba!")
        try:
            os.remove("english.50MB_clean_"+pr)
        except:
            print("el english no estaba!")
else:
    print("Porfa escriban bien o usen -h pa cachar")
