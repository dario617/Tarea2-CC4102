

def mismaspreguntas():
    dna = "dna.50MB_clean_2^17"
    f = open(dna)
    print(f"Estoy usando {dna}")
    #eng = "english.50MB_clean_2^15"
    #f = open(eng)
    #print(f"Estoy usando {eng}")
    s = f.readline()
    print(f"String de largo {len(s)}")
    query = "o"
    print(f"Cantidad de {query} en el texto: {s.count(query)}")
    query = "a"
    print(f"Cantidad de {query} en el texto: {s.count(query)}")
    query = "e"
    print(f"Cantidad de {query} en el texto: {s.count(query)}")
    query = "atata"
    print(f"Cantidad de {query} en el texto: {s.count(query)}")

mismaspreguntas()
