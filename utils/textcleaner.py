import re

largos = [2**i for i in range(10,24)]

def cleanAlphaNumeric(filename: str, newfilepreffix: str, limit: int):
    with open(filename, 'r', encoding='cp1252') as source:
        with open("clean_"+newfilepreffix+"_"+filename, 'w') as dest:
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

def generateEnglishTexts():
    for i, largo in enumerate(largos):
        preffix = "2^"+str(i+10)
        print(preffix)
        cleanAlphaNumeric("english.50MB", preffix, largo)

generateEnglishTexts()
