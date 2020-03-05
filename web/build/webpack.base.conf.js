'use strict'
const path = require('path')
const webpack = require('webpack')
const utils = require('./utils')
const config = require('../config')
const LodashWebpackPlugin = require('lodash-webpack-plugin');
function resolve (dir) {
    return path.join(__dirname, '..', dir)
}

const createLintingRule = () => ({
    test: /\.(js|jsx)$/,
    loader: 'eslint-loader',
    enforce: 'pre',
    include: [resolve('src'), resolve('test')],
    options: {
        formatter: require('eslint-friendly-formatter'),
        emitWarning: !config.dev.showEslintErrorsInOverlay
    }
})

module.exports = {
    context: path.resolve(__dirname, '../'),
    entry:
    //['babel-polyfill', './src/main/frontend/src/main.js'],         // 做兼容时
    {
        app: './src/main/frontend/src/main.js'
    },
    output: {
        // path: 表示生成文件的根目录
        path: config.build.assetsRoot,
        filename: '[name].js',
        publicPath: process.env.NODE_ENV === 'production'
            ? config.build.assetsPublicPath
            : config.dev.assetsPublicPath
    },
    resolve: {
        extensions: ['.js', 'jsx', '.json'],
        alias: {
            utils: path.resolve(__dirname, '../src/main/frontend/src/utils'),
        }
    },
    module: {
    rules: [
        ...(config.dev.useEslint ? [createLintingRule()] : []),
        {
            test: /\.js$/,
            loader: 'babel-loader',
            include: [resolve('src'), resolve('test'), resolve('node_modules/webpack-dev-server/client')],
            options: {
                plugins: [
                    ['import', { libraryName: "antd", style: true }]
                ]
            },
        },
        {
            test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
            loader: 'url-loader',
            options: {
                limit: 10000,
                name: utils.assetsPath('images/[name].[ext]')
            }
        },
        {
            test: /\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/,
            loader: 'url-loader',
            options: {
                limit: 10000,
                name: utils.assetsPath('media/[name].[hash:7].[ext]')
            }
        },
        {
            test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
            loader: 'url-loader',
            options: {
                limit: 10000,
                name: utils.assetsPath('fonts/[name].[ext]')
            }
        }
    ]
    },
    plugins: [
        new LodashWebpackPlugin({ shorthands:true, collection:true, cloning: true, paths: true }),
        new webpack.ProvidePlugin({
            request: 'utils/request',
            $:'jquery'
        })
    ],
    // node: {
    //     // prevent webpack from injecting useless setImmediate polyfill because Vue
    //     // source contains it (although only uses it if it's native).
    //     setImmediate: false,
    //     // prevent webpack from injecting mocks to Node native modules
    //     // that does not make sense for the client
    //     dgram: 'empty',
    //     fs: 'empty',
    //     net: 'empty',
    //     tls: 'empty',
    //     child_process: 'empty'
    // }
}
