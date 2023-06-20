#!/usr/bin/env node

const path = require('node:path');
const fs = require('node:fs');

const pak = JSON.parse(
  fs.readFileSync(path.join(__dirname, 'package.json'), 'utf8'),
);
const file = path.join(__dirname, 'env.ts');
console.log(`📝 Generate the ${pak.name}@${pak.version} env file: ${file}`);
const content = `export const test = true;
export const appKey = '';
export const id = '';
export const ps = '';
export const agoraAppId = '';
export const accountType = 'easemob'; // agora or easemob
export const targetId = '';
`;
fs.writeFileSync(file, content, 'utf-8');
