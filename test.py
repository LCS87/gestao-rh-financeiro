import glob
import os

files = glob.glob('e:/ProjetoNovo/gestao-rh-financeiro/src/main/java/br/com/callcenter/**/*Controller.java', recursive=True)
for f in files:
    with open(f, 'r', encoding='utf-8') as file:
        lines = file.read().split('\n')
    
    modified = False
    for i, line in enumerate(lines):
        if '@DeleteMapping' in lines[i-1] and '@PreAuthorize' in line:
            lines[i] = '    @PreAuthorize(\"hasRole(\\'TI\\')\")'
            modified = True
            
    if modified:
        with open(f, 'w', encoding='utf-8') as file:
            file.write('\n'.join(lines))
        print(f'Updated {f}')
