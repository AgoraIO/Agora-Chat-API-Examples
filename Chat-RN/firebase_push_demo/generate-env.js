#!/usr/bin/env node

const path = require('node:path');
const fs = require('node:fs');

const project_root = path.resolve(__dirname);

const file = path.join(project_root, 'env.ts');
const content = `
export const test = false;
export const appKey = '';
export const id = '';
export const ps = '';
export const agoraAppId = '';
export const accountType = 'agora'; // agora or easemob
export const targetId = '';
export const senderId = '';
export const requestGetTokenUrl = '';
export const requestRegistryAccountUrl = '';
`;
if (fs.existsSync(file) === false) {
  fs.writeFileSync(file, content, 'utf-8');
}
